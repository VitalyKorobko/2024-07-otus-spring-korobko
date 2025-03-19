package ru.otus.hw.config;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.InetAddress;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CancellationException;
import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.scheduler.Scheduler;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.util.retry.Retry;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.GROUP_INSTANCE_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_RECORDS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

@Slf4j
public class ReactiveReceiver<T> {
    public static final int MAX_POLL_INTERVAL_MS = 1_000;

    private final Random random = new Random();

    private final Disposable kafkaSubscriber;

    private final Disposable kafkaConnection;

    public ReactiveReceiver(String bootstrapServers,
                            Class<?> valueClass,
                            String topicName,
                            Scheduler schedulerValueReceiver,
                            String groupId,
                            Consumer<T> valueConsumer) {
        Properties props = getProps(bootstrapServers, groupId, valueClass);

        ReceiverOptions<Long, T> receiverOptions = getReceiveOptions(props, schedulerValueReceiver, topicName);

        Flux<ConsumerRecord<Long, T>> inboundFlux = inboundFlux(receiverOptions);

        hook();

        var kafkaFlow = inboundFlux.doOnCancel(() -> log.info("connection canceled"))
                .doOnError(error -> log.error("Consuming error", error))
                .publish();

        log.info("start consuming");
        kafkaConnection = kafkaFlow.connect();
        kafkaSubscriber =
                kafkaFlow.subscribe(
                        receiverRecord -> {
                            var key = receiverRecord.key();
                            var value = receiverRecord.value();
                            log.info("key:{}, value:{}, record:{}", key, value, receiverRecord);
                            valueConsumer.accept(value);
                        });
    }

    private void hook() {
        Hooks.onErrorDropped(
                error -> {
                    if (error instanceof CancellationException) {
                        log.info("Cancellation event:", error);
                    } else {
                        log.error("error:", error);
                    }
                });
    }

    private Flux<ConsumerRecord<Long,T>> inboundFlux(ReceiverOptions<Long, T> receiverOptions) {
        return KafkaReceiver.create(receiverOptions)
                .receiveAutoAck()
                .concatMap(
                        consumerRecordFlux -> {
                            log.info("consumerRecordFlux done, commit");
                            return consumerRecordFlux;
                        })
                .retryWhen(Retry.backoff(3, Duration.of(5, ChronoUnit.SECONDS)));
    }

    private ReceiverOptions<Long, T> getReceiveOptions(Properties props,
                                                       Scheduler schedulerValueReceiver,
                                                       String topicName) {
        return ReceiverOptions.<Long, T>create(props)
                .pollTimeout(Duration.ofSeconds(500))
                .schedulerSupplier(() -> schedulerValueReceiver)
                .subscription(Collections.singleton(topicName));
    }

    public void close() {
        log.info("stop consuming");
        kafkaSubscriber.dispose();
        kafkaConnection.dispose();
    }

    private String makeGroupInstanceIdConfig(String groupId) {
        try {
            var hostName = InetAddress.getLocalHost().getHostName();
            return String.join(
                    "-",
                    groupId,
                    hostName,
                    String.valueOf(random.nextInt(100_999_999)));
        } catch (Exception ex) {
            throw new ConsumerException("can't make GroupInstanceIdConfig", ex);
        }
    }

    private Properties getProps(String bootstrapServers, String groupId, Class<?> valueClass) {
        var objectMapper = new ObjectMapper();
        var props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(GROUP_ID_CONFIG, groupId);
        props.put(GROUP_INSTANCE_ID_CONFIG, makeGroupInstanceIdConfig(groupId));
        props.put(ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        props.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        props.put(JsonDeserializer.OBJECT_MAPPER, objectMapper);
        props.put(JsonDeserializer.TYPE_REFERENCE, objectMapper.getTypeFactory().constructType(valueClass));
        props.put(MAX_POLL_RECORDS_CONFIG, 3);
        props.put(MAX_POLL_INTERVAL_MS_CONFIG, MAX_POLL_INTERVAL_MS);

        return props;
    }

}

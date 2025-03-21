package ru.otus.hw.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.BodyInserters;
import ru.otus.hw.model.OrderValue;
import ru.otus.hw.model.Request;
import ru.otus.hw.model.Response;
import ru.otus.hw.model.RequestId;
import ru.otus.hw.model.ResponseId;
import ru.otus.hw.model.Order;
import ru.otus.hw.processor.MailProcessor;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.annotation.NonNull;
import ru.otus.hw.service.DiscoveryService;
import ru.otus.hw.service.TokenStorageService;

@Configuration
@Slf4j
public class ApplicationConfig {
    private static final String AUTHORIZATION = "Authorization";

    private static final String BEARER = "Bearer ";

    private static final int THREAD_POOL_SIZE = 2;

    private static final int REQUEST_RECEIVER_POOL_SIZE = 1;

    private static final int KAFKA_POOL_SIZE = 1;

    private final AtomicLong responseIdGenerator = new AtomicLong(0);


    @Bean(name = "serverThreadEventLoop", destroyMethod = "close")
    public NioEventLoopGroup serverThreadEventLoop() {
        return new NioEventLoopGroup(THREAD_POOL_SIZE,
                new ThreadFactory() {
                    private final AtomicLong threadIdGenerator = new AtomicLong(0);

                    @Override
                    public Thread newThread(@NonNull Runnable task) {
                        return new Thread(task, "in-proc-server-thread-" + threadIdGenerator.incrementAndGet());
                    }
                });
    }

    @Bean
    public ReactiveWebServerFactory reactiveWebServerFactory(
            @Qualifier("serverThreadEventLoop") NioEventLoopGroup serverThreadEventLoop) {
        var factory = new NettyReactiveWebServerFactory();
        factory.addServerCustomizers(builder -> builder.runOn(serverThreadEventLoop));
        return factory;
    }

    @Bean(name = "clientThreadEventLoop", destroyMethod = "close")
    public NioEventLoopGroup clientThreadEventLoop() {
        return new NioEventLoopGroup(THREAD_POOL_SIZE,
                new ThreadFactory() {
                    private final AtomicLong threadIdGenerator = new AtomicLong(0);

                    @Override
                    public Thread newThread(@NonNull Runnable task) {
                        return new Thread(task, "in-proc-client-thread-" + threadIdGenerator.incrementAndGet());
                    }
                });
    }

    @Bean
    public ReactorResourceFactory reactorResourceFactory(
            @Qualifier("clientThreadEventLoop") NioEventLoopGroup clientThreadEventLoop) {
        var resourceFactory = new ReactorResourceFactory();
        resourceFactory.setLoopResources(b -> clientThreadEventLoop);
        resourceFactory.setUseGlobalResources(false);
        return resourceFactory;
    }

    @Bean
    public ReactorClientHttpConnector reactorClientHttpConnector(ReactorResourceFactory resourceFactory) {
        return new ReactorClientHttpConnector(resourceFactory, mapper -> mapper);
    }

    @Bean
    public Scheduler timer() {
        return Schedulers.newParallel("in-proc-scheduler-processor-thread", 2);
    }


    @Bean("requestReceiverScheduler")
    public Scheduler requestReceiverScheduler() {
        return Schedulers.newParallel("request-receiver", REQUEST_RECEIVER_POOL_SIZE);
    }

    @Bean("kafkaScheduler")
    public Scheduler kafkaScheduler() {
        return Schedulers.newParallel("kafka-scheduler", KAFKA_POOL_SIZE);
    }

    @Bean
    public WebClient notificationWebClient(WebClient.Builder builder,
                                           @Value("${application.notification.name}") String serviceName,
                                           DiscoveryService discoveryService) {
        var url = "http://" + discoveryService.getHostName(serviceName) + ":" + discoveryService.getPort(serviceName);
        log.info("URL: %s".formatted(url));
        return builder
                .baseUrl(url)
                .build();
    }

    @Bean
    public RestClient authWebClient(@Value("${application.auth_service.name}") String serviceName,
                                    DiscoveryService discoveryService) {
        var url = "http://" + discoveryService.getHostName(serviceName) + ":" + discoveryService.getPort(serviceName);
        log.info("URL: %s".formatted(url));
        return RestClient.builder()
                .baseUrl(url)
                .build();
    }

    @Bean(destroyMethod = "close")
    public ReactiveSender<OrderValue, Response> responseSender(
            @Value("${application.kafka-bootstrap-servers}") String bootstrapServers,
            @Value("${application.topic-response}") String topicResponse,
            @Qualifier("kafkaScheduler") Scheduler kafkaScheduler
    ) {
        return new ReactiveSender<>(bootstrapServers, kafkaScheduler, topicResponse);
    }

    @Bean(destroyMethod = "close")
    public ReactiveReceiver<Request> requestReceiver(
            @Value("${application.kafka-bootstrap-servers}") String bootstrapServers,
            @Value("${application.topic-request}") String topicRequest,
            @Value("${application.kafka-group-id}") String groupId,
            @Qualifier("requestReceiverScheduler") Scheduler responseReceiverScheduler,
            ReactiveSender<OrderValue, Response> responseSender,
            MailProcessor<Order> mailProcessor,
            @Qualifier("notificationWebClient") WebClient webClient,
            TokenStorageService tokenStorageService) {

        return new ReactiveReceiver<>(
                bootstrapServers,
                Request.class,
                topicRequest,
                responseReceiverScheduler,
                groupId,
                request -> webClient.post().uri(String.format("/api/v1/order"))
                        .body(BodyInserters.fromValue(request.data()))
                        .header(AUTHORIZATION, BEARER + tokenStorageService
                                .findByUsername(request.data().username()))
                        .retrieve()
                        .bodyToMono(Order.class)
                        .map(mailProcessor::process)
                        .flatMap(order ->
                                responseSender.send(new Response(new ResponseId(responseIdGenerator.incrementAndGet()),
                                                new OrderValue(new RequestId(request.id()), order)),
                                        orderValueDataForSending ->
                                                log.info("response send:{}", orderValueDataForSending))
                        )
                        .subscribe());
    }

}

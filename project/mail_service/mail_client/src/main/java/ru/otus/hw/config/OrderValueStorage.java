package ru.otus.hw.config;

import ru.otus.hw.model.OrderValue;
import ru.otus.hw.model.RequestId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.publisher.Sinks;
import reactor.util.annotation.NonNull;

public class OrderValueStorage implements Sinks.EmitFailureHandler {
    private final Sinks.Many<ResponseData> sink;

    private final ConnectableFlux<ResponseData> sinkConnectable;

    private static final Logger log = LoggerFactory.getLogger(OrderValueStorage.class);

    public OrderValueStorage() {
        sink = Sinks.many().multicast().onBackpressureBuffer();
        sinkConnectable = sink.asFlux().publish();
        sinkConnectable.connect();
    }

    public void put(RequestId requestId, OrderValue value) {
        log.info("put. requestId:{}, value:{}", requestId, value);
        sink.emitNext(new ResponseData(requestId, value), this);
    }

    public Mono<OrderValue> get(RequestId requestId) {
        return Mono.from(sinkConnectable
                .filter(responseData -> {
                    log.info("waiting:{}, fact:{}", requestId, responseData.requestId);
                    return responseData.requestId.id() == requestId.id();
                })
                .map(responseData -> responseData.orderValue));
    }

    @Override
    public boolean onEmitFailure(@NonNull SignalType signalType, @NonNull Sinks.EmitResult emitResult) {
        return false;
    }

    private record ResponseData(RequestId requestId, OrderValue orderValue) {
    }
}

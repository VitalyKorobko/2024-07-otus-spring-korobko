package ru.otus.hw.config;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.publisher.Sinks;
import reactor.util.annotation.NonNull;
import ru.otus.hw.model.OrderValue;
import ru.otus.hw.model.RequestId;

@Slf4j
public class OrderValueStorage implements Sinks.EmitFailureHandler {
    private final Sinks.Many<ResponseData> sink;

    private final ConnectableFlux<ResponseData> sinkConnectable;

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

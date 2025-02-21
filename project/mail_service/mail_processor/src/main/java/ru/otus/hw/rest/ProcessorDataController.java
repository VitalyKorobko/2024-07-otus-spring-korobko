package ru.otus.hw.rest;


import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.BodyInserters;
import ru.otus.hw.model.Order;
import ru.otus.hw.processor.DataProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class ProcessorDataController {
    private static final Logger log = LoggerFactory.getLogger(ProcessorDataController.class);

    private final DataProcessor<Order> dataProcessor;

    private final WebClient webClient;

    public ProcessorDataController(WebClient webClient,
                                   @Qualifier("dataProcessorMono") DataProcessor<Order> dataProcessor) {
        this.webClient = webClient;
        this.dataProcessor = dataProcessor;
    }

    @GetMapping(value = "/api/v1/order")
    public Mono<Order> dataMono(@RequestBody Order order) {
        log.info("request for data, order:{}", order);

        var srcRequest = webClient.post().uri(String.format("/api/v1/order"))
                .body(BodyInserters.fromValue(order))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Order.class)
                .doOnNext(oder-> log.info("processor return order: {}", order));

        return srcRequest;
    }

}

package ru.otus.hw.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Response implements DataForSending<OrderValue> {
    private final ResponseId id;

    private final OrderValue orderValue;

    @JsonCreator
    public Response(@JsonProperty("id") ResponseId id, @JsonProperty("orderValue") OrderValue orderValue) {
        this.id = id;
        this.orderValue = orderValue;
    }

    @Override
    public long id() {
        return id.id();
    }

    @Override
    public OrderValue data() {
        return orderValue;
    }

    @Override
    public String toString() {
        return "Response{" +
                "id=" + id +
                ", orderValue=" + orderValue +
                '}';
    }
}

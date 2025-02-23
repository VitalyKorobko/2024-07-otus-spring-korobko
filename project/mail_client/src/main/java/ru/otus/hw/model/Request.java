package ru.otus.hw.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Request implements DataForSending<Order> {
    private final RequestId id;

    private final Order order;

    @JsonCreator
    public Request(@JsonProperty("id") RequestId id, @JsonProperty("order") Order order) {
        this.id = id;
        this.order = order;
    }

    @Override
    public long id() {
        return id.id();
    }

    @Override
    public Order data() {
        return order;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", order=" + order +
                '}';
    }
}

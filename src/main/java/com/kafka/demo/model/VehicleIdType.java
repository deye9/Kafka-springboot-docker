package com.kafka.demo.model;

import java.util.Objects;

public record VehicleIdType(
    String value
) {
    public VehicleIdType {
        Objects.requireNonNull(value);
    }
}
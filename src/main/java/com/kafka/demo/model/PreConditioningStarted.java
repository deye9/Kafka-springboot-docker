package com.kafka.demo.model;

import java.util.Objects;

public record PreConditioningStarted(
    String updated,
    String value
) {
    public PreConditioningStarted {
        Objects.requireNonNull(updated);
        Objects.requireNonNull(value);
    }
}
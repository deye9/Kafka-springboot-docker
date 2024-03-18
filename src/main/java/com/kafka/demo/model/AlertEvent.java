package com.kafka.demo.model;

import java.util.Objects;

public record AlertEvent(
    PreConditioningStarted preConditioningStarted
) {
    public AlertEvent {
        Objects.requireNonNull(preConditioningStarted);
    }
}
package com.kafka.demo.model;

import java.util.Objects;

public record MessageObject(
    String notificationType,
    String vehicleId,
    Identifiers identifiers,
    String created,
    Status current,
    Status previous
) {
    public MessageObject {
        Objects.requireNonNull(notificationType);
        Objects.requireNonNull(vehicleId);
        Objects.requireNonNull(identifiers);
        Objects.requireNonNull(created);
        Objects.requireNonNull(current);
        Objects.requireNonNull(previous);
    }
}

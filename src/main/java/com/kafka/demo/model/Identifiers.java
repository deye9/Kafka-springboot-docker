package com.kafka.demo.model;

import java.util.Objects;

public record Identifiers(
    String vehicleId,
    VehicleIdType vehicleIdType
) {
    public Identifiers {
        Objects.requireNonNull(vehicleId);
        Objects.requireNonNull(vehicleIdType);
    }
}
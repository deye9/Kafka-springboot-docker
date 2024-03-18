package com.kafka.demo.model;

import java.util.Map;

public record Status(
    Map<String, AlertEvent> alerts
) {
    public Status {
        if (alerts != null) {
            alerts.forEach((key, value) -> {
                if (value == null) {
                    throw new IllegalArgumentException("Null value in alerts");
                }
            });
        }
    }
}
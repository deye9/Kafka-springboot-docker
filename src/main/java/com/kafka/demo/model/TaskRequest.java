package com.kafka.demo.model;

import java.util.Objects;

public record TaskRequest(String name) {

  public TaskRequest {
    Objects.requireNonNull(name);
  }
  
}

package com.kafka.demo.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public record MessageObject(String taskId, String taskName, float percentageComplete, Optional<Status> status)
    implements Serializable {

  public MessageObject {
    Objects.requireNonNull(taskId);
    Objects.requireNonNull(taskName);
    Objects.requireNonNull(percentageComplete);
  }

  public enum Status {
    SUBMITTED, STARTED, RUNNING, FINISHED, TERMINATED
  }
}

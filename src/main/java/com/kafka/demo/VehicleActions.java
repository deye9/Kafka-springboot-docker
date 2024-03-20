package com.kafka.demo;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VehicleActions {
  @JsonProperty("doorOpen")
  DOOR_OPEN("TSDP-DoorStatus#SideDoorStatus"),
  
  @JsonProperty("doorClose")
  DOOR_CLOSE("TSDP-DoorStatus#SideDoorStatus"),
  
  @JsonProperty("hoodOpen")
  HOOD_OPEN("TSDP-DoorStatus#BonnetStatus"),
  
  @JsonProperty("hoodClose")
  HOOD_CLOSE("TSDP-DoorStatus#BonnetStatus"),
  
  @JsonProperty("trunkOpen")
  TRUNK_OPEN("TSDP-DoorStatus#TailgateStatus"),
  
  @JsonProperty("trunkClose")
  TRUNK_CLOSE("TSDP-TailgateStatus"),
  
  @JsonProperty("windowOpen")
  WINDOW_OPEN("TSDP-CentralWindowPos"),
  
  @JsonProperty("windowClose")
  WINDOW_CLOSE("TSDP-CentralWindowPos"),
  
  @JsonProperty("vehicleLocked")
  VEHICLE_LOCKED("TSDP-CentralLockStatus"),
  
  @JsonProperty("vehicleUnlocked")
  VEHICLE_UNLOCKED("TSDP-DoorStatus#CentralLockStatus"),
  
  @JsonProperty("preConditioningStarted")
  PRECONDITIONING_STARTED("TSDP-PreClimateStatus"),
  
  @JsonProperty("preConditioningEnded")
  PRECONDITIONING_ENDED("TSDP-PreClimateStatus");
  
  private final String actionCode;
}


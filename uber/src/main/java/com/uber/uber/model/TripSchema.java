package com.uber.uber.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Document(collection = "trips")
@Data
public class TripSchema {
    @Id
    private ObjectId id;
    @NotNull
    private String userId;
    private String driverId;
    @NotNull
    private String pickupLocation;

    @NotNull
    private String destination;
    @NotNull
    private String rideOtp;
    private TripStatus status;
    private List<String> route;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private double pickupLat;
    private double pickupLon;
    private double driverLat;
    private double driverLon;
    private double destinationLat;
    private double destinationLon;
    private double distance;
    private double price;
    private Set<String> notifiedDrivers = ConcurrentHashMap.newKeySet();

    // Nested enum
    public enum TripStatus {
        REQUESTED, DRIVER_ASSIGNED, STARTED, COMPLETED, CANCELLED
    }
}
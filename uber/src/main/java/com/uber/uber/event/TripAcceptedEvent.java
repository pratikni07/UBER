package com.uber.uber.event;

public class TripAcceptedEvent {
    private final String tripId;
    private final String driverId;
    private final double driverLat;
    private final double driverLon;

    public TripAcceptedEvent(String tripId, String driverId, double driverLat, double driverLon) {
        this.tripId = tripId;
        this.driverId = driverId;
        this.driverLat = driverLat;
        this.driverLon = driverLon;
    }

    // Getters
    public String getTripId() { return tripId; }
    public String getDriverId() { return driverId; }
    public double getDriverLat() { return driverLat; }
    public double getDriverLon() { return driverLon; }
}
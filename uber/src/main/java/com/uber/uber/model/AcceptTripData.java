package com.uber.uber.model;

import lombok.Data;

@Data
public class AcceptTripData {
    private String tripId;
    private double driverLat;
    private double driverLon;

}

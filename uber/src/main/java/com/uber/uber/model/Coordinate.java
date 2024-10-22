package com.uber.uber.model;

public class Coordinate {
    private double latitude;
    private double longitude;

    // Getter and Setter for latitude
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    // Getter and Setter for longitude
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    // Constructor for easier instantiation
    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

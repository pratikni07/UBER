package com.uber.uber.model;

import java.util.List;

public class Route {
    private List<Coordinate> coordinates;
    private double distance;
    private double duration;

    // Getter and Setter for coordinates
    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public Route setCoordinates(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    // Getter and Setter for distance
    public double getDistance() {
        return distance;
    }

    public Route setDistance(double distance) {
        this.distance = distance;
        return this;
    }

    // Getter and Setter for duration
    public double getDuration() {
        return duration;
    }

    public Route setDuration(double duration) {
        this.duration = duration;
        return this;
    }
}

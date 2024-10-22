package com.uber.uber.service;

import com.uber.uber.model.UserSchema;
import com.uber.uber.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import ch.hsr.geohash.GeoHash;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DriverSearchService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final int PRECISION = 7;

    public Map<String, Double> findNearestDriversWithDistance(double latitude, double longitude, double radiusInKm) {
        Point pickupPoint = new Point(longitude, latitude);
        Distance distance = new Distance(radiusInKm, Metrics.KILOMETERS);

        Query query = new Query(Criteria.where("role").is("DRIVER")
                .and("status").is("ACTIVE")
                .and("location").nearSphere(pickupPoint).maxDistance(distance.getValue()));

        List<UserSchema> nearbyDrivers = mongoTemplate.find(query, UserSchema.class);

        return nearbyDrivers.stream()
                .collect(Collectors.toMap(
                        UserSchema::getStringId,
                        driver -> calculateDistance(pickupPoint, new Point(driver.getLocation()[0], driver.getLocation()[1]))
                ));
    }

    private double calculateDistance(Point point1, Point point2) {
        double earthRadius = 6371; // in kilometers
        double lat1 = Math.toRadians(point1.getY());
        double lat2 = Math.toRadians(point2.getY());
        double lon1 = Math.toRadians(point1.getX());
        double lon2 = Math.toRadians(point2.getX());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }

    public void updateDriverLocation(String driverId, double latitude, double longitude) {
        UserSchema driver = userRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        driver.setLocation(new double[]{longitude, latitude});
        driver.setGeoHash(GeoHash.geoHashStringWithCharacterPrecision(latitude, longitude, PRECISION));
        userRepository.save(driver);
    }
}
package com.uber.uber.service;

import com.uber.uber.event.TripAcceptedEvent;
import com.uber.uber.model.TripSchema;
import com.uber.uber.repository.TripRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private DriverSearchService driverSearchService;

    @Autowired
    @Lazy
    private SocketIOService socketIOService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private OtpService otpService;

    private static final int BATCH_SIZE = 10;
    private static final double INITIAL_SEARCH_RADIUS = 5.0;
    private static final double MAX_SEARCH_RADIUS = 20.0;
    private static final long DRIVER_RESPONSE_TIMEOUT = 30;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public TripSchema requestTrip(String userId, String pickupLocation, String destination, double pickupLat, double pickupLon) {
        TripSchema trip = new TripSchema();
        trip.setUserId(userId);
        trip.setPickupLocation(pickupLocation);
        trip.setDestination(destination);
        trip.setStatus(TripSchema.TripStatus.REQUESTED);
        trip.setCreatedAt(LocalDateTime.now());
        trip.setUpdatedAt(LocalDateTime.now());
        trip.setPickupLat(pickupLat);
        trip.setPickupLon(pickupLon);

        String otp = otpService.generateOtp();
        trip.setRideOtp(otp);

        trip = tripRepository.save(trip);

        notifyNextBatchOfDrivers(trip);

        return trip;
    }

    public void notifyNextBatchOfDrivers(TripSchema trip) {
        double currentRadius = INITIAL_SEARCH_RADIUS;
        Map<String, Double> nearbyDriversWithDistance = new ConcurrentHashMap<>();

        while (nearbyDriversWithDistance.size() < BATCH_SIZE && currentRadius <= MAX_SEARCH_RADIUS) {
            Map<String, Double> drivers = driverSearchService.findNearestDriversWithDistance(
                    trip.getPickupLat(), trip.getPickupLon(), currentRadius);
            for (Map.Entry<String, Double> entry : drivers.entrySet()) {
                if (!trip.getNotifiedDrivers().contains(entry.getKey())) {
                    nearbyDriversWithDistance.put(entry.getKey(), entry.getValue());
                    if (nearbyDriversWithDistance.size() == BATCH_SIZE) {
                        break;
                    }
                }
            }
            currentRadius += 5.0;
        }

        if (!nearbyDriversWithDistance.isEmpty()) {
            socketIOService.notifyNearbyDrivers(trip, nearbyDriversWithDistance);
            trip.getNotifiedDrivers().addAll(nearbyDriversWithDistance.keySet());
            tripRepository.save(trip);
            scheduleNextBatchNotification(trip);
        } else {
            handleNoAvailableDrivers(trip);
        }
    }

    private void scheduleNextBatchNotification(TripSchema trip) {
        scheduler.schedule(() -> {
            TripSchema updatedTrip = getTripById(trip.getId().toString());
            if (updatedTrip.getStatus() == TripSchema.TripStatus.REQUESTED) {
                notifyNextBatchOfDrivers(updatedTrip);
            }
        }, DRIVER_RESPONSE_TIMEOUT, TimeUnit.SECONDS);
    }

    public boolean assignDriverToTrip(String tripId, String driverId, double driverLat, double driverLon) {
        Query query = new Query(Criteria.where("id").is(tripId).and("status").is(TripSchema.TripStatus.REQUESTED));
        Update update = new Update()
                .set("driverId", driverId)
                .set("status", TripSchema.TripStatus.DRIVER_ASSIGNED)
                .set("updatedAt", LocalDateTime.now())
                .set("driverLat", driverLat)
                .set("driverLon", driverLon);

        boolean assigned = mongoTemplate.updateFirst(query, update, TripSchema.class).getModifiedCount() > 0;

        if (assigned) {
            TripSchema updatedTrip = getTripById(tripId);
            socketIOService.notifyUser(updatedTrip.getUserId(), "driverAssigned", updatedTrip);
            socketIOService.notifyOtherDrivers(tripId, driverId);
        }
        return assigned;
    }

    public TripSchema acceptTrip(String tripId, String driverId, double driverLat, double driverLon) {
        TripSchema trip = tripRepository.findById(new ObjectId(tripId))
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (trip.getStatus() != TripSchema.TripStatus.REQUESTED) {
            throw new RuntimeException("Trip is no longer available");
        }

        trip.setDriverId(driverId);
        trip.setDriverLat(driverLat);
        trip.setDriverLon(driverLon);
        trip.setStatus(TripSchema.TripStatus.DRIVER_ASSIGNED);
        trip.setUpdatedAt(LocalDateTime.now());

        trip = tripRepository.save(trip);

        socketIOService.notifyTripAccepted(trip);

        return trip;
    }

    public TripSchema getTripById(String tripId) {
        return tripRepository.findById(new ObjectId(tripId))
                .orElseThrow(() -> new RuntimeException("Trip not found"));
    }

    public TripSchema updateTripStatus(String tripId, TripSchema.TripStatus newStatus) {
        TripSchema trip = getTripById(tripId);
        trip.setStatus(newStatus);
        trip.setUpdatedAt(LocalDateTime.now());
        return tripRepository.save(trip);
    }

    public TripSchema pickupTrip(String tripId, String RouteOtp, String DriverId) {
        TripSchema trip = getTripById(tripId);
        if (!trip.getRideOtp().equals(RouteOtp)) {
            throw new RuntimeException("Invalid OTP");
        }

        trip.setStatus(TripSchema.TripStatus.STARTED);
        trip.setUpdatedAt(LocalDateTime.now());
        return tripRepository.save(trip);
    }

    private void handleNoAvailableDrivers(TripSchema trip) {
        // Implement logic to handle cases when no drivers are available
        // This could involve notifying the user, putting the trip in a queue, etc.
        socketIOService.notifyUser(trip.getUserId(), "noDriversAvailable", trip);
    }
}
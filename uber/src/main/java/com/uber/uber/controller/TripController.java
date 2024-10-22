package com.uber.uber.controller;

import com.uber.uber.model.TripSchema;
import com.uber.uber.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    @Autowired
    private TripService tripService;


    @PostMapping
    public ResponseEntity<TripSchema> requestTrip(@RequestBody TripSchema tripRequest) {
        TripSchema trip = tripService.requestTrip(
                tripRequest.getUserId(),
                tripRequest.getPickupLocation(),
                tripRequest.getDestination(),
                tripRequest.getPickupLat(),
                tripRequest.getPickupLon()
        );
        return ResponseEntity.ok(trip);
    }



    @GetMapping("/{tripId}")
    public ResponseEntity<TripSchema> getTripById(@PathVariable String tripId) {
        TripSchema trip = tripService.getTripById(tripId);
        return ResponseEntity.ok(trip);
    }

    //    pickup

    @PostMapping("/{tripId}/pickup")
    public ResponseEntity<TripSchema> pickupTrip(@PathVariable String tripId , @RequestBody String RouteOtp, String DriverId) {
        TripSchema trip = tripService.pickupTrip(tripId, RouteOtp, DriverId);
        return ResponseEntity.ok(trip);
    }

    @PutMapping("/{tripId}/status")
    public ResponseEntity<TripSchema> updateTripStatus(
            @PathVariable String tripId,
            @RequestBody TripSchema statusUpdate) {
        TripSchema updatedTrip = tripService.updateTripStatus(tripId, statusUpdate.getStatus());
        return ResponseEntity.ok(updatedTrip);
    }
}

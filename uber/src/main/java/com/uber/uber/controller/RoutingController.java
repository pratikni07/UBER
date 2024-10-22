package com.uber.uber.controller;

import com.uber.uber.model.Route;
import com.uber.uber.service.RoutingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/route")

public class RoutingController {
    private final RoutingService routingService;
    public RoutingController(RoutingService routingService) {
        this.routingService = routingService;
    }
    @GetMapping
    public ResponseEntity<Route> getShortestRoute(
            @RequestParam double startLat,
            @RequestParam double startLon,
            @RequestParam double endLat,
            @RequestParam double endLon) {
        Route shortestRoute = routingService.findShortestRoute(startLat, startLon, endLat, endLon);
        return ResponseEntity.ok(shortestRoute);
    }
}

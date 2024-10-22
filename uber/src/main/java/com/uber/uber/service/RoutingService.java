package com.uber.uber.service;

import com.uber.uber.algorithm.DijkstraRoutingAlgorithm;
import com.uber.uber.model.Route;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoutingService {

    private final OSRMService osrmService;
    private final DijkstraRoutingAlgorithm dijkstraAlgorithm;

    public RoutingService(OSRMService osrmService) {
        this.osrmService = osrmService;
        Map<Integer, Map<Integer, Double>> graph = new HashMap<>();
        this.dijkstraAlgorithm = new DijkstraRoutingAlgorithm(graph);
    }

    public Route findShortestRoute(double startLat, double startLon, double endLat, double endLon) {
        Route osrmRoute = osrmService.getRoute(startLat, startLon, endLat, endLon);
        List<Integer> refinedPath = dijkstraAlgorithm.findShortestPath(0, 1);
        return new Route()
                .setCoordinates( osrmRoute.getCoordinates())
                .setDistance( osrmRoute.getDistance())
                .setDuration( osrmRoute.getDuration());
    }
}

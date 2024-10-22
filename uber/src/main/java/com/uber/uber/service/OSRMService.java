package com.uber.uber.service;

import com.uber.uber.model.Coordinate;
import com.uber.uber.model.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

@Service
public class OSRMService {
    private final RestTemplate restTemplate;
    private final String osrmBaseUrl;

    public OSRMService(RestTemplate restTemplate, @Value("${osrm.base.url}") String osrmBaseUrl) {
        this.restTemplate = restTemplate;
        this.osrmBaseUrl = osrmBaseUrl;
    }

    public Route getRoute(double startLat, double startLon, double endLat, double endLon) {
        String url = String.format("%s/route/v1/driving/%f,%f;%f,%f?overview=full&geometries=geojson",
                osrmBaseUrl, startLon, startLat, endLon, endLat);

        JsonNode response = restTemplate.getForObject(url, JsonNode.class);

        if (response != null && response.has("routes") && response.get("routes").size() > 0) {
            JsonNode route = response.get("routes").get(0);
            double distance = route.get("distance").asDouble();
            double duration = route.get("duration").asDouble();
            List<Coordinate> coordinates = parseCoordinates(route.get("geometry").get("coordinates"));

            return new Route().setCoordinates(coordinates).setDistance(distance).setDuration(duration);
        }

        throw new RuntimeException("No route found");
    }

    private List<Coordinate> parseCoordinates(JsonNode coordinatesNode) {
        List<Coordinate> coordinates = new ArrayList<>();
        for (JsonNode coord : coordinatesNode) {
            double lon = coord.get(0).asDouble();
            double lat = coord.get(1).asDouble();
            coordinates.add(new Coordinate(lat, lon));
        }
        return coordinates;
    }
}
package com.uber.uber.service;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.uber.uber.event.TripAcceptedEvent;
import com.uber.uber.model.AcceptTripData;
import com.uber.uber.model.TripSchema;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SocketIOService {

    @Autowired
    private SocketIOServer socketIOServer;

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private TripService tripService;

    private final Map<String, SocketIOClient> driverClients = new ConcurrentHashMap<>();
    private final Map<String, SocketIOClient> userClients = new ConcurrentHashMap<>();

    @PostConstruct
    private void init() {
        socketIOServer.addConnectListener(client -> {
            String driverId = client.getHandshakeData().getSingleUrlParam("driverId");
            String userId = client.getHandshakeData().getSingleUrlParam("userId");
            if (driverId != null) {
                driverClients.put(driverId, client);
            } else if (userId != null) {
                userClients.put(userId, client);
            }
        });

        socketIOServer.addDisconnectListener(client -> {
            String driverId = client.getHandshakeData().getSingleUrlParam("driverId");
            String userId = client.getHandshakeData().getSingleUrlParam("userId");
            if (driverId != null) {
                driverClients.remove(driverId);
            } else if (userId != null) {
                userClients.remove(userId);
            }
        });

        socketIOServer.addEventListener("acceptTrip", AcceptTripData.class, (client, data, ackRequest) -> {
            String driverId = client.getHandshakeData().getSingleUrlParam("driverId");
            if (driverId != null) {
                try {
                    TripSchema acceptedTrip = tripService.acceptTrip(data.getTripId(), driverId, data.getDriverLat(), data.getDriverLon());
                    client.sendEvent("tripAcceptedConfirmation", acceptedTrip);
                } catch (Exception e) {
                    client.sendEvent("tripAcceptError", e.getMessage());
                }
            }
        });

        socketIOServer.start();
    }

    @PreDestroy
    private void destroy() {
        if (socketIOServer != null) {
            socketIOServer.stop();
        }
    }


    public void notifyTripAccepted(TripSchema trip) {
        SocketIOClient userClient = getUserClient(trip.getUserId());
        if (userClient != null && userClient.isChannelOpen()) {
            userClient.sendEvent("tripAccepted", trip);
        }
    }

    public SocketIOClient getUserClient(String userId) {
        return userClients.get(userId);
    }


    public void notifyNearbyDrivers(TripSchema trip, Map<String, Double> nearbyDriversWithDistance) {
        nearbyDriversWithDistance.forEach((driverId, distance) -> {
            SocketIOClient client = driverClients.get(driverId);
            if (client != null && client.isChannelOpen()) {
                client.sendEvent("newTripRequest", trip);
            }
        });
    }

    public void notifyOtherDrivers(String tripId, String assignedDriverId) {
        driverClients.forEach((driverId, client) -> {
            if (!driverId.equals(assignedDriverId) && client.isChannelOpen()) {
                client.sendEvent("tripNoLongerAvailable", tripId);
            }
        });
    }

    public void notifyUser(String userId, String event, Object data) {
        SocketIOClient client = userClients.get(userId);
        if (client != null && client.isChannelOpen()) {
            client.sendEvent(event, data);
        }
    }
}
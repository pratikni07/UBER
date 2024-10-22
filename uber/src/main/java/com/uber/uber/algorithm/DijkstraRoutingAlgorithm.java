package com.uber.uber.algorithm;

import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class DijkstraRoutingAlgorithm {

    private final Map<Integer, Map<Integer, Double>> graph;

    public DijkstraRoutingAlgorithm(Map<Integer, Map<Integer, Double>> graph) {
        this.graph = graph;
    }

    public List<Integer> findShortestPath(int start, int end) {
        Map<Integer, Double> distances = new HashMap<>();
        Map<Integer, Integer> previousNodes = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distance));

        for (int node : graph.keySet()) {
            distances.put(node, Double.MAX_VALUE);
            previousNodes.put(node, null);
        }

        distances.put(start, 0.0);
        queue.offer(new Node(start, 0.0));

        while (!queue.isEmpty()) {
            int current = queue.poll().id;

            if (current == end) {
                break;
            }

            if (!graph.containsKey(current)) {
                continue;
            }

            for (Map.Entry<Integer, Double> neighbor : graph.get(current).entrySet()) {
                int nextNode = neighbor.getKey();
                double newDist = distances.get(current) + neighbor.getValue();

                if (newDist < distances.get(nextNode)) {
                    distances.put(nextNode, newDist);
                    previousNodes.put(nextNode, current);
                    queue.offer(new Node(nextNode, newDist));
                }
            }
        }

        return reconstructPath(previousNodes, end);
    }

    private List<Integer> reconstructPath(Map<Integer, Integer> previousNodes, int end) {
        List<Integer> path = new ArrayList<>();
        for (Integer at = end; at != null; at = previousNodes.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }

    private static class Node {
        int id;
        double distance;

        Node(int id, double distance) {
            this.id = id;
            this.distance = distance;
        }
    }
}
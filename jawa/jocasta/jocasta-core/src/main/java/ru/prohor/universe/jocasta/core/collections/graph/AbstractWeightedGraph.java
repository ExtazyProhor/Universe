package ru.prohor.universe.jocasta.core.collections.graph;

import ru.prohor.universe.jocasta.core.collections.tuple.Tuple2;
import ru.prohor.universe.jocasta.core.functional.TriPredicate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public abstract class AbstractWeightedGraph<T> implements AbstractGraph<T> {
    protected final Map<T, Map<T, Integer>> adjacencyList;

    public AbstractWeightedGraph() {
        adjacencyList = new HashMap<>();
    }

    @Override
    public int verticesCount() {
        return adjacencyList.size();
    }

    @Override
    public boolean containsVertex(T value) {
        return adjacencyList.containsKey(value);
    }

    @Override
    public boolean containsEdge(T firstVertex, T secondVertex) {
        return containsVertex(firstVertex) && adjacencyList.get(firstVertex).containsKey(secondVertex);
    }

    @Override
    public void addVertex(T value) {
        adjacencyList.put(value, new HashMap<>());
    }

    public abstract void addEdge(T firstVertex, T secondVertex, int length);

    protected void addVerticesIfNotExists(T firstVertex, T secondVertex) {
        if (!containsVertex(firstVertex))
            addVertex(firstVertex);
        if (!containsVertex(secondVertex))
            addVertex(secondVertex);
    }

    @Override
    public Set<T> getIncidentVertices(T value) {
        return adjacencyList.get(value).keySet();
    }

    @Override
    public Set<T> getAllVertices() {
        return adjacencyList.keySet();
    }

    public List<WeightedDirectedEdge<T>> getAllDirectedEdges() {
        List<WeightedDirectedEdge<T>> allDirectedEdges = new ArrayList<>();
        for (Map.Entry<T, Map<T, Integer>> entry : adjacencyList.entrySet()) {
            for (Map.Entry<T, Integer> edge : entry.getValue().entrySet()) {
                allDirectedEdges.add(new WeightedDirectedEdge<>(edge.getValue(), entry.getKey(), edge.getKey()));
            }
        }
        return allDirectedEdges;
    }

    public Map<T, Integer> getIncidentEdges(T value) {
        return adjacencyList.get(value);
    }

    public Integer getLengthBetween(T firstVertex, T secondVertex) {
        if (!containsVertex(firstVertex))
            return null;
        return getIncidentEdges(firstVertex).get(secondVertex);
    }


    private static final RuntimeException GRAPH_HAS_CYCLE = new RuntimeException("graph has a negative cycle");

    public Map<T, Integer> shortestPathsLengthDijkstra(T source) {
        Map<T, Integer> distances = new HashMap<>();
        PriorityQueue<Tuple2<T, Integer>> priorityQueue = new PriorityQueue<>(Comparator.comparing(Tuple2::get2));

        for (T vertex : getAllVertices())
            distances.put(vertex, Integer.MAX_VALUE);
        distances.put(source, 0);
        priorityQueue.add(new Tuple2<>(source, 0));

        while (!priorityQueue.isEmpty()) {
            Tuple2<T, Integer> current = priorityQueue.poll();
            T currentVertex = current.get1();
            int currentWeight = current.get2();

            for (Map.Entry<T, Integer> edge : getIncidentEdges(currentVertex).entrySet()) {
                T neighbor = edge.getKey();
                int newDist = currentWeight + edge.getValue();
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    priorityQueue.add(new Tuple2<>(neighbor, newDist));
                }
            }
        }
        return distances;
    }

    public Map<T, T> previousVerticesDijkstra(T source) {
        Map<T, T> previousVertices = new HashMap<>();
        Map<T, Integer> distances = new HashMap<>();
        PriorityQueue<Tuple2<T, Integer>> priorityQueue = new PriorityQueue<>(Comparator.comparing(Tuple2::get2));

        for (T vertex : getAllVertices()) {
            distances.put(vertex, Integer.MAX_VALUE);
        }
        distances.put(source, 0);
        priorityQueue.add(new Tuple2<>(source, 0));

        while (!priorityQueue.isEmpty()) {
            Tuple2<T, Integer> current = priorityQueue.poll();
            T currentVertex = current.get1();
            int currentWeight = current.get2();

            for (Map.Entry<T, Integer> edge : getIncidentEdges(currentVertex).entrySet()) {
                T neighbor = edge.getKey();
                int newDist = currentWeight + edge.getValue();
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previousVertices.put(neighbor, currentVertex);
                    priorityQueue.add(new Tuple2<>(neighbor, newDist));
                }
            }
        }
        return previousVertices;
    }

    public List<T> shortestPathBetweenDijkstra(T firstVertex, T secondVertex) {
        return shortestPathBetween(firstVertex, secondVertex, previousVerticesDijkstra(firstVertex));
    }

    public Integer shortestPathLengthBetweenDijkstra(T firstVertex, T secondVertex) {
        return shortestPathsLengthDijkstra(firstVertex).getOrDefault(secondVertex, null);
    }

    public Map<T, Integer> shortestPathsLengthBellmanFord(T source) {
        List<WeightedDirectedEdge<T>> allEdges = getAllDirectedEdges();
        Map<T, Integer> distances = new HashMap<>();
        distances.put(source, 0);
        TriPredicate<Integer, Integer, Integer> checkTwoDistances = (from, to, weight) -> {
            if (from == null)
                return false;
            if (to == null)
                return true;
            return to > from + weight;
        };
        boolean wasChange;

        for (int i = 0; i < verticesCount(); ++i) {
            wasChange = false;
            for (WeightedDirectedEdge<T> edge : allEdges) {
                Integer distanceToPrevious = distances.get(edge.from());
                Integer distanceToNext = distances.get(edge.to());
                if (checkTwoDistances.test(distanceToPrevious, distanceToNext, edge.weight())) {
                    distances.put(edge.to(), distanceToPrevious + edge.weight());
                    wasChange = true;
                }
            }
            if (!wasChange)
                break;
            if (i == verticesCount() - 1)
                throw GRAPH_HAS_CYCLE;
        }
        return distances;
    }

    public Map<T, T> previousVerticesBellmanFord(T source) {
        Map<T, T> previousVertices = new HashMap<>();
        List<WeightedDirectedEdge<T>> allEdges = getAllDirectedEdges();
        Map<T, Integer> distances = new HashMap<>();
        distances.put(source, 0);
        TriPredicate<Integer, Integer, Integer> checkTwoDistances = (from, to, weight) -> {
            if (from == null)
                return false;
            if (to == null)
                return true;
            return to > from + weight;
        };
        boolean wasChange;

        for (int i = 0; i < verticesCount(); ++i) {
            wasChange = false;
            for (WeightedDirectedEdge<T> edge : allEdges) {
                Integer distanceToPrevious = distances.get(edge.from());
                Integer distanceToNext = distances.get(edge.to());
                if (checkTwoDistances.test(distanceToPrevious, distanceToNext, edge.weight())) {
                    distances.put(edge.to(), distanceToPrevious + edge.weight());
                    previousVertices.put(edge.to(), edge.from());
                    wasChange = true;
                }
            }
            if (!wasChange)
                break;
            if (i == verticesCount() - 1)
                throw GRAPH_HAS_CYCLE;
        }
        return previousVertices;
    }

    public List<T> shortestPathBetweenBellmanFord(T firstVertex, T secondVertex) {
        return shortestPathBetween(firstVertex, secondVertex, previousVerticesBellmanFord(firstVertex));
    }

    public Integer shortestPathLengthBetweenBellmanFord(T firstVertex, T secondVertex) {
        return shortestPathsLengthBellmanFord(firstVertex).getOrDefault(secondVertex, null);
    }

    private List<T> shortestPathBetween(T firstVertex, T secondVertex, Map<T, T> previousVertices) {
        if (!previousVertices.containsKey(secondVertex))
            return null;
        LinkedList<T> path = new LinkedList<>();
        while (!secondVertex.equals(firstVertex)) {
            path.addFirst(secondVertex);
            secondVertex = previousVertices.get(secondVertex);
        }
        return path;
    }

    public Map<T, Map<T, Integer>> shortestPathsLengthFloydWarshall() {
        Map<T, Map<T, Integer>> distances = new HashMap<>(adjacencyList);
        Set<T> vertices = getAllVertices();
        for (T vertex : vertices) {
            distances.get(vertex).put(vertex, 0);
        }

        for (T k : vertices) {
            for (T i : vertices) {
                for (T j : vertices) {
                    if (!distances.get(i).containsKey(k) || !distances.get(k).containsKey(j))
                        continue;
                    int sumDistance = distances.get(i).get(k) + distances.get(k).get(j);
                    if (!distances.get(i).containsKey(j) || sumDistance < distances.get(i).get(j))
                        distances.get(i).put(j, sumDistance);
                }
            }
        }

        for (T vertex : vertices) {
            if (distances.get(vertex).get(vertex) != 0)
                throw GRAPH_HAS_CYCLE;
        }
        return distances;
    }
}

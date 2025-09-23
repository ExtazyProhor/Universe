package ru.prohor.universe.jocasta.core.collections.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractUnweightedGraph<T> implements AbstractGraph<T> {
    protected final Map<T, Set<T>> adjacencyList;

    public AbstractUnweightedGraph() {
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
        return containsVertex(firstVertex) && adjacencyList.get(firstVertex).contains(secondVertex);
    }

    @Override
    public void addVertex(T value) {
        adjacencyList.put(value, new HashSet<>());
    }

    public abstract void addEdge(T firstVertex, T secondVertex);

    protected void addVerticesIfNotExists(T firstVertex, T secondVertex) {
        if (!containsVertex(firstVertex))
            addVertex(firstVertex);
        if (!containsVertex(secondVertex))
            addVertex(secondVertex);
    }

    @Override
    public Set<T> getIncidentVertices(T value) {
        return adjacencyList.get(value);
    }

    @Override
    public Set<T> getAllVertices() {
        return adjacencyList.keySet();
    }
}

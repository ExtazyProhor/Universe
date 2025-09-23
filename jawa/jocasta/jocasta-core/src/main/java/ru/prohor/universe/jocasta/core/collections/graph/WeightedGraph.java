package ru.prohor.universe.jocasta.core.collections.graph;

import ru.prohor.universe.jocasta.core.collections.common.DisjointSetUnion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

public class WeightedGraph<T> extends AbstractWeightedGraph<T> {
    public WeightedGraph() {
        super();
    }

    @Override
    public void addEdge(T firstVertex, T secondVertex, int length) {
        addVerticesIfNotExists(firstVertex, secondVertex);
        adjacencyList.get(firstVertex).put(secondVertex, length);
        adjacencyList.get(secondVertex).put(firstVertex, length);
    }

    public void addEdge(WeightedEdge<T> edge) {
        addEdge(edge.v1(), edge.v2(), edge.weight());
    }

    public WeightedGraph<T> getMinimumSpanningTreeKruskal() {
        if (verticesCount() == 0)
            return new WeightedGraph<>();
        WeightedGraph<T> mst = new WeightedGraph<>();
        Set<T> allVertices = getAllVertices();
        mst.addAllVertices(allVertices);
        List<WeightedEdge<T>> edges = getSortedEdges();
        DisjointSetUnion<T> setUnion = new DisjointSetUnion<>(allVertices);
        for (WeightedEdge<T> edge : edges) {
            if (setUnion.inSameSet(edge.v1(), edge.v2()))
                continue;
            mst.addEdge(edge);
            setUnion.unite(edge.v1(), edge.v2());
        }
        return mst;
    }

    public WeightedGraph<T> getMinimumSpanningTreePrim() {
        if (verticesCount() == 0)
            return new WeightedGraph<>();
        WeightedGraph<T> mst = new WeightedGraph<>();
        PriorityQueue<WeightedEdge<T>> edges = new PriorityQueue<>();
        T currentVertex = getAnyVertex();
        mst.addVertex(currentVertex);
        while (mst.verticesCount() < this.verticesCount()) {
            for (Map.Entry<T, Integer> edge : getIncidentEdges(currentVertex).entrySet())
                if (!mst.containsVertex(edge.getKey()))
                    edges.add(new WeightedEdge<>(edge.getValue(), currentVertex, edge.getKey()));
            while (mst.containsVertex(Objects.requireNonNull(edges.peek()).v2()))
                edges.poll();
            if (edges.isEmpty())
                throw new RuntimeException("graph is not connected");
            WeightedEdge<T> addedEdge = edges.poll();
            mst.addEdge(addedEdge);
            currentVertex = addedEdge.v2();
        }
        return mst;
    }

    private List<WeightedEdge<T>> getSortedEdges() {
        Set<WeightedEdge<T>> edges = new HashSet<>();
        for (Map.Entry<T, Map<T, Integer>> vertex : adjacencyList.entrySet()) {
            for (Map.Entry<T, Integer> incident : vertex.getValue().entrySet()) {
                edges.add(new WeightedEdge<>(incident.getValue(), vertex.getKey(), incident.getKey()));
            }
        }
        List<WeightedEdge<T>> list = new ArrayList<>(edges);
        Collections.sort(list);
        return list;
    }
}

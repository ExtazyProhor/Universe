package ru.prohor.universe.jocasta.core.collections.graph;

public class WeightedDigraph<T> extends AbstractWeightedGraph<T> implements AbstractDigraph<T> {
    public WeightedDigraph() {
        super();
    }

    @Override
    public void addEdge(T firstVertex, T secondVertex, int length) {
        addVerticesIfNotExists(firstVertex, secondVertex);
        adjacencyList.get(firstVertex).put(secondVertex, length);
    }
}

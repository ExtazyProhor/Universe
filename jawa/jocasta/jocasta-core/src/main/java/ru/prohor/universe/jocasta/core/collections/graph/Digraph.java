package ru.prohor.universe.jocasta.core.collections.graph;

public class Digraph<T> extends AbstractUnweightedGraph<T> implements AbstractDigraph<T> {
    public Digraph() {
        super();
    }

    @Override
    public void addEdge(T firstVertex, T secondVertex) {
        addVerticesIfNotExists(firstVertex, secondVertex);
        adjacencyList.get(firstVertex).add(secondVertex);
    }
}

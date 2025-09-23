package ru.prohor.universe.jocasta.core.collections.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

public interface AbstractGraph<T> {
    int verticesCount();

    boolean containsVertex(T value);

    boolean containsEdge(T firstVertex, T secondVertex);

    default boolean containsAllVertices(Iterable<T> values) {
        for (T t : values)
            if (!containsVertex(t))
                return false;
        return true;
    }

    default boolean containsAtLeastOneVertex(Iterable<T> values) {
        for (T t : values)
            if (containsVertex(t))
                return true;
        return false;
    }

    void addVertex(T value);

    default void addAllVertices(Iterable<T> values) {
        values.forEach(this::addVertex);
    }

    Set<T> getIncidentVertices(T value);

    Set<T> getAllVertices();

    default T getAnyVertex() {
        return getAllVertices().stream().findAny().orElse(null);
    }

    default boolean searchBFS(T vertex, T target) {
        Queue<T> queue = new LinkedList<>();
        Set<T> used = new HashSet<>();
        queue.add(vertex);

        while (!queue.isEmpty()) {
            T current = queue.poll();
            if (used.contains(current))
                continue;
            used.add(current);
            if (current.equals(target))
                return true;
            queue.addAll(getIncidentVertices(current));
        }
        return false;
    }

    default List<T> shortestPathBFS(T vertex, T target) {
        Queue<T> queue = new LinkedList<>();
        queue.add(vertex);
        Map<T, T> previous = new HashMap<>();
        previous.put(vertex, null);

        while (!queue.isEmpty()) {
            T current = queue.poll();

            if (current.equals(target)) {
                LinkedList<T> path = new LinkedList<>();
                for (T t = target; t != null; t = previous.get(t)) {
                    path.addFirst(t);
                }
                path.removeFirst();
                return path;
            }

            for (T neighbor : getIncidentVertices(current)) {
                if (!previous.containsKey(neighbor)) {
                    queue.add(neighbor);
                    previous.put(neighbor, current);
                }
            }
        }
        return null;
    }

    default void visitingBFS(T vertex, Consumer<T> consumer) {
        Queue<T> queue = new LinkedList<>();
        Set<T> used = new HashSet<>();
        queue.add(vertex);

        while (!queue.isEmpty()) {
            T current = queue.poll();
            if (used.contains(current))
                continue;
            used.add(current);
            consumer.accept(current);
            queue.addAll(getIncidentVertices(current));
        }
    }

    default boolean searchDFS(T vertex, T searched) {
        LinkedList<T> stack = new LinkedList<>();
        Set<T> used = new HashSet<>();
        stack.addLast(vertex);

        while (!stack.isEmpty()) {
            T current = stack.pollLast();
            if (used.contains(current))
                continue;
            used.add(current);
            if (current.equals(searched))
                return true;
            stack.addAll(getIncidentVertices(current));
        }
        return false;
    }

    default void visitingDFS(T vertex, Consumer<T> consumer) {
        LinkedList<T> stack = new LinkedList<>();
        Set<T> used = new HashSet<>();
        stack.addLast(vertex);

        while (!stack.isEmpty()) {
            T current = stack.pollLast();
            if (used.contains(current))
                continue;
            used.add(current);
            consumer.accept(current);
            stack.addAll(getIncidentVertices(current));
        }
    }
}

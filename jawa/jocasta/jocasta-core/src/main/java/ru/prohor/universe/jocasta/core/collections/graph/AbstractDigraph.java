package ru.prohor.universe.jocasta.core.collections.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AbstractDigraph<T> extends AbstractGraph<T> {
    default boolean containsBidirectionalEdge(T firstVertex, T secondVertex) {
        return containsEdge(firstVertex, secondVertex) && containsEdge(secondVertex, firstVertex);
    }

    default List<T> topologicalSortingBasedDFS() {
        List<T> list = new LinkedList<>();
        LinkedList<T> stack = new LinkedList<>();
        Set<T> used = new HashSet<>();

        for (T current : getAllVertices()) {
            stack.addLast(current);
            while (!stack.isEmpty()) {
                T fromStack = stack.peekLast();
                if (used.contains(fromStack)) {
                    stack.pollLast();
                    if (!list.contains(fromStack)) {
                        list.addFirst(fromStack);
                    }
                    continue;
                }
                used.add(fromStack);
                stack.addAll(getIncidentVertices(fromStack));
            }
        }
        return list;
    }

    default List<T> topologicalSortingByKahn() {
        List<T> list = new LinkedList<>();

        Map<T, Integer> inDegreeCounter = new HashMap<>();
        for (T vertex : getAllVertices())
            inDegreeCounter.put(vertex, 0);
        for (T vertex : getAllVertices()) {
            for (T incidentVertex : getIncidentVertices(vertex))
                inDegreeCounter.put(incidentVertex, inDegreeCounter.getOrDefault(incidentVertex, 0) + 1);
        }
        int count = verticesCount();
        while (!inDegreeCounter.isEmpty()) {
            for (Map.Entry<T, Integer> entry : inDegreeCounter.entrySet()) {
                if (entry.getValue() == 0) {
                    list.add(entry.getKey());
                    inDegreeCounter.remove(entry.getKey());
                    for (T incident : getIncidentVertices(entry.getKey())) {
                        int newCount = inDegreeCounter.get(incident) - 1;
                        inDegreeCounter.put(incident, newCount);
                    }
                    break;
                }
            }
            if (inDegreeCounter.size() == count)
                throw new RuntimeException("Graph has cycle");
            count = inDegreeCounter.size();
        }
        return list;
    }
}

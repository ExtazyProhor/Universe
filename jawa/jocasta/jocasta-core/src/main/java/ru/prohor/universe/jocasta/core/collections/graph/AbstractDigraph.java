package ru.prohor.universe.jocasta.core.collections.graph;

import ru.prohor.universe.jocasta.core.collections.common.Counter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
                        list.add(0, fromStack);
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

        Counter<T> inDegreeCounter = new Counter<>();
        //for (T t : getAllVertices())
        //    inDegreeCounter.set(t, 0);
        for (T vertex : getAllVertices())
            for (T incidentVertex : getIncidentVertices(vertex))
                inDegreeCounter.inc(incidentVertex);

        int count = inDegreeCounter.size();
        while (!inDegreeCounter.isEmpty()) {
            for (Counter.Count<T> entry : inDegreeCounter.counts()) {
                if (entry.value() == 0) {
                    list.add(entry.key());
                    inDegreeCounter.remove(entry.key());
                    for (T incident : getIncidentVertices(entry.key()))
                        inDegreeCounter.dec(incident);
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

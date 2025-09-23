package ru.prohor.universe.jocasta.core.collections.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DisjointSetUnion<T> {
    private final Map<T, T> parents;

    public DisjointSetUnion() {
        parents = new HashMap<>();
    }

    public DisjointSetUnion(Set<T> elements) {
        parents = new HashMap<>();
        makeSets(elements);
    }

    public boolean contains(T element) {
        return parents.containsKey(element);
    }

    public void makeSet(T element) {
        parents.put(element, element);
    }

    public void makeSets(Set<T> elements) {
        elements.forEach(this::makeSet);
    }

    public T find(T element) {
        if (element.equals(parents.get(element)))
            return element;
        T parent = find(parents.get(element));
        parents.put(element, parent);
        return parent;
    }

    public void unite(T first, T second) {
        first = find(first);
        second = find(second);
        if ((first.hashCode() + second.hashCode()) % 2 == 0)
            parents.put(first, second);
        else
            parents.put(second, first);
    }

    public boolean inSameSet(T first, T second) {
        return find(first).equals(find(second));
    }

    public int sizeOfSet(T element) {
        T root = find(element);
        int size = 0;
        for (T e : parents.keySet())
            if (find(e).equals(root))
                size++;
        return size;
    }

    public void clear() {
        parents.clear();
    }

    public int size() {
        return parents.size();
    }
}

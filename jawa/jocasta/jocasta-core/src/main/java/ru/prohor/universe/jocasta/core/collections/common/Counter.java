package ru.prohor.universe.jocasta.core.collections.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Counter<T> {
    private final Map<T, Integer> map;

    public Counter() {
        this.map = new HashMap<>();
    }

    public Counter(int initialCapacity) {
        this.map = new HashMap<>(initialCapacity);
    }

    public boolean hasCount(T key) {
        return map.containsKey(key);
    }

    public int count(T key) {
        return map.getOrDefault(key, 0);
    }

    public int inc(T key) {
        return add(key, 1);
    }

    public int dec(T key) {
        return add(key, -1);
    }

    public int sub(T key, int count) {
        return add(key, -count);
    }

    public int add(T key, int count) {
        int current = map.getOrDefault(key, 0);
        current += count;
        if (current == 0)
            map.remove(key);
        else
            map.put(key, current);
        return current;
    }

    public List<Count<T>> counts() {
        return map.entrySet()
                .stream()
                .map(entry -> new Count<>(entry.getKey(), entry.getValue()))
                .toList();
    }

    public record Count<T>(
            T key,
            int value
    ) {}
}

package ru.prohor.universe.jocasta.core.collections.graph;

import java.util.Objects;

public record WeightedDirectedEdge<T>(int weight, T from, T to) implements Comparable<WeightedDirectedEdge<T>> {
    @Override
    public int compareTo(WeightedDirectedEdge<T> o) {
        return Integer.compare(this.weight, o.weight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        WeightedDirectedEdge<?> that = (WeightedDirectedEdge<?>) o;
        return (this.weight == that.weight && this.from.equals(that.from) && this.to.equals(that.to));
    }

    @Override
    public int hashCode() {
        return Objects.hash(weight, from.hashCode(), to.hashCode());
    }
}

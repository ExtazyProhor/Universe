package ru.prohor.universe.jocasta.core.collections.graph;

import java.util.Objects;

public record WeightedEdge<T>(int weight, T v1, T v2) implements Comparable<WeightedEdge<T>> {
    @Override
    public int compareTo(WeightedEdge<T> o) {
        return Integer.compare(this.weight, o.weight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        WeightedEdge<?> that = (WeightedEdge<?>) o;
        if (this.weight != that.weight)
            return false;
        return Objects.equals(this.v1, that.v1) && Objects.equals(this.v2, that.v2) ||
                Objects.equals(this.v1, that.v2) && Objects.equals(this.v2, that.v1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weight, v1.hashCode() + v2.hashCode());
    }
}

package ru.prohor.universe.jocasta.core.collections.common;

import jakarta.annotation.Nonnull;
import ru.prohor.universe.jocasta.core.collections.tuple.Tuple2;
import ru.prohor.universe.jocasta.core.functional.DiFunction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class CartesianProduct {
    private CartesianProduct() {}

    public static CartesianProductOfTwoRanges ofTwoRanges(int to) {
        return ofTwoRanges(to, to);
    }

    public static CartesianProductOfTwoRanges ofTwoRanges(int to1, int to2) {
        return ofTwoRanges(0, to1, 0, to2);
    }

    public static CartesianProductOfTwoRanges ofTwoRanges(int from1, int to1, int from2, int to2) {
        return new CartesianProductOfTwoRanges(Range.of(from1, to1), Range.of(from2, to2));
    }

    private record Range(int from, int to, int size) implements Iterable<Integer> {
        private static Range of(int from, int to) {
            if (from > to) {
                throw new IllegalArgumentException("'from' (" + from + ") must be less or equals than 'to' (" + to + ")");
            }
            return new Range(from, to, to - from);
        }

        @Override
        @Nonnull
        public Iterator<Integer> iterator() {
            return new Iterator<>() {
                int current = from;

                @Override
                public boolean hasNext() {
                    return current < to;
                }

                @Override
                public Integer next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return current++;
                }
            };
        }
    }

    public static class CartesianProductOfTwoRanges {
        private final Range range1;
        private final Range range2;

        private CartesianProductOfTwoRanges(Range range1, Range range2) {
            this.range1 = range1;
            this.range2 = range2;
        }

        public <T> List<T> map(DiFunction<Integer, Integer, T> mapper) {
            List<T> list = new ArrayList<>(range1.size * range2.size);
            for (int i : range1)
                for (int j : range2)
                    list.add(mapper.apply(i, j));
            return list;
        }

        public List<Tuple2<Integer, Integer>> asTuplesList() {
            List<Tuple2<Integer, Integer>> list = new ArrayList<>(range1.size * range2.size);
            for (int i : range1)
                for (int j : range2)
                    list.add(new Tuple2<>(i, j));
            return list;
        }
    }
}

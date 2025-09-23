package ru.prohor.universe.jocasta.core.collections.common;

import ru.prohor.universe.jocasta.core.functional.MonoPredicate;

import java.util.Iterator;

public class Range {
    private static final int DEFAULT_FROM = 0;
    private static final int DEFAULT_STEP = 1;

    private Range() {}

    public static Iterable<Integer> range(int to) {
        return range(DEFAULT_FROM, to);
    }

    public static Iterable<Integer> range(int from, int to) {
        return range(from, to, DEFAULT_STEP);
    }

    public static Iterable<Integer> range(int from, int to, int step) {
        if (step == 0)
            throw new RuntimeException("Step must not be 0");
        MonoPredicate<Integer> hasNext = cursor -> step > 0 ? cursor < to : cursor > to;
        return () -> new IntRangeIterator(hasNext, step, from);
    }

    public static class IntRangeIterator implements Iterator<Integer> {
        private final MonoPredicate<Integer> hasNext;
        private final int step;
        private int cursor;

        public IntRangeIterator(MonoPredicate<Integer> hasNext, int step, int from) {
            this.hasNext = hasNext;
            this.step = step;
            this.cursor = from;
        }

        public boolean hasNext() {
            return hasNext.test(cursor);
        }

        public Integer next() {
            cursor += step;
            return cursor - step;
        }
    }
}

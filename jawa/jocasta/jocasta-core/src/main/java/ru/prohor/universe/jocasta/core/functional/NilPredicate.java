package ru.prohor.universe.jocasta.core.functional;

@FunctionalInterface
public interface NilPredicate {
    boolean test();

    default NilPredicate and(NilPredicate other) {
        return () -> test() && other.test();
    }

    default NilPredicate negate() {
        return () -> !test();
    }

    default NilPredicate or(NilPredicate other) {
        return () -> test() || other.test();
    }
}

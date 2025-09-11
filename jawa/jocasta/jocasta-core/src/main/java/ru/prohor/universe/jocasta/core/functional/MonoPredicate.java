package ru.prohor.universe.jocasta.core.functional;

import java.util.function.Predicate;

@FunctionalInterface
public interface MonoPredicate<T1> extends Predicate<T1> {
    @Override
    boolean test(T1 t1);
}

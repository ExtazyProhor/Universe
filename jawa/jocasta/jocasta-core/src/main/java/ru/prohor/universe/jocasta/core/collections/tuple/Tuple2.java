package ru.prohor.universe.jocasta.core.collections.tuple;

import ru.prohor.universe.jocasta.core.functional.DiFunction;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;

/**
 * This file is a modified version of the original file developed by Stepan Koltsov at Yandex.
 * Original code is available under <a href="http://www.apache.org/licenses/LICENSE-2.0">the Apache License 2.0</a>
 * at <a href="https://github.com/v1ctor/bolts">github.com/v1ctor/bolts</a>
 */
public class Tuple2<T1, T2> extends AbstractTuple {
    private final T1 t1;
    private final T2 t2;

    public Tuple2(T1 t1, T2 t2) {
        super(2);
        this.t1 = t1;
        this.t2 = t2;
    }

    @Override
    public Object get(int n) {
        return switch (n) {
            case 1 -> t1;
            case 2 -> t2;
            default -> throw new IllegalArgumentException();
        };
    }

    public T1 get1() {
        return t1;
    }

    public T2 get2() {
        return t2;
    }

    public <R> R reduce(DiFunction<T1, T2, R> reducer) {
        return reducer.apply(t1, t2);
    }

    public <R> Tuple2<R, T2> map1(MonoFunction<T1, R> f) {
        return new Tuple2<>(f.apply(t1), t2);
    }

    public <R> Tuple2<T1, R> map2(MonoFunction<T2, R> f) {
        return new Tuple2<>(t1, f.apply(t2));
    }

    public Tuple2<T2, T1> swap() {
        return new Tuple2<>(t2, t1);
    }

    @SuppressWarnings("unchecked")
    public <T3, T4> Tuple2<T3, T4> uncheckedCast() {
        return (Tuple2<T3, T4>) this;
    }
}

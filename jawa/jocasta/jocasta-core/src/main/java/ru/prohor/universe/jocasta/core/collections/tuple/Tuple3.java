package ru.prohor.universe.jocasta.core.collections.tuple;

import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.core.functional.TriFunction;

/**
 * This file is a modified version of the original file developed by Stepan Koltsov at Yandex.
 * Original code is available under <a href="http://www.apache.org/licenses/LICENSE-2.0">the Apache License 2.0</a>
 * at <a href="https://github.com/v1ctor/bolts">github.com/v1ctor/bolts</a>
 */
public class Tuple3<T1, T2, T3> extends AbstractTuple {
    private final T1 t1;
    private final T2 t2;
    private final T3 t3;

    public Tuple3(T1 t1, T2 t2, T3 t3) {
        super(3);
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    @Override
    public Object get(int n) {
        return switch (n) {
            case 1 -> t1;
            case 2 -> t2;
            case 3 -> t3;
            default -> throw new IllegalArgumentException();
        };
    }

    public T1 get1() {
        return t1;
    }

    public T2 get2() {
        return t2;
    }

    public T3 get3() {
        return t3;
    }

    public <R> R reduce(TriFunction<T1, T2, T3, R> reducer) {
        return reducer.apply(t1, t2, t3);
    }

    public <R> Tuple3<R, T2, T3> map1(MonoFunction<T1, R> f) {
        return new Tuple3<>(f.apply(t1), t2, t3);
    }

    public <R> Tuple3<T1, R, T3> map2(MonoFunction<T2, R> f) {
        return new Tuple3<>(t1, f.apply(t2), t3);
    }

    public <R> Tuple3<T1, T2, R> map3(MonoFunction<T3, R> f) {
        return new Tuple3<>(t1, t2, f.apply(t3));
    }

    @SuppressWarnings("unchecked")
    public <T4, T5, T6> Tuple3<T4, T5, T6> uncheckedCast() {
        return (Tuple3<T4, T5, T6>) this;
    }
}

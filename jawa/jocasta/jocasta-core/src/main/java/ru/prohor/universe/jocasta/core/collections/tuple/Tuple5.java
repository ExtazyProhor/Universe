package ru.prohor.universe.jocasta.core.collections.tuple;

import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.core.functional.PentaFunction;

/**
 * This file is a modified version of the original file developed by Stepan Koltsov at Yandex.
 * Original code is available under <a href="http://www.apache.org/licenses/LICENSE-2.0">the Apache License 2.0</a>
 * at <a href="https://github.com/v1ctor/bolts">github.com/v1ctor/bolts</a>
 */
public class Tuple5<T1, T2, T3, T4, T5> extends AbstractTuple {
    private final T1 t1;
    private final T2 t2;
    private final T3 t3;
    private final T4 t4;
    private final T5 t5;

    public Tuple5(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        super(5);
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
        this.t5 = t5;
    }

    @Override
    public Object get(int n) {
        return switch (n) {
            case 1 -> t1;
            case 2 -> t2;
            case 3 -> t3;
            case 4 -> t4;
            case 5 -> t5;
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

    public T4 get4() {
        return t4;
    }

    public T5 get5() {
        return t5;
    }

    public <R> R reduce(PentaFunction<T1, T2, T3, T4, T5, R> reducer) {
        return reducer.apply(t1, t2, t3, t4, t5);
    }

    public <R> Tuple5<R, T2, T3, T4, T5> map1(MonoFunction<T1, R> f) {
        return new Tuple5<>(f.apply(t1), t2, t3, t4, t5);
    }

    public <R> Tuple5<T1, R, T3, T4, T5> map2(MonoFunction<T2, R> f) {
        return new Tuple5<>(t1, f.apply(t2), t3, t4, t5);
    }

    public <R> Tuple5<T1, T2, R, T4, T5> map3(MonoFunction<T3, R> f) {
        return new Tuple5<>(t1, t2, f.apply(t3), t4, t5);
    }

    public <R> Tuple5<T1, T2, T3, R, T5> map4(MonoFunction<T4, R> f) {
        return new Tuple5<>(t1, t2, t3, f.apply(t4), t5);
    }

    public <R> Tuple5<T1, T2, T3, T4, R> map5(MonoFunction<T5, R> f) {
        return new Tuple5<>(t1, t2, t3, t4, f.apply(t5));
    }

    @SuppressWarnings("unchecked")
    public <T6, T7, T8, T9, T10> Tuple5<T6, T7, T8, T9, T10> uncheckedCast() {
        return (Tuple5<T6, T7, T8, T9, T10>) this;
    }
}

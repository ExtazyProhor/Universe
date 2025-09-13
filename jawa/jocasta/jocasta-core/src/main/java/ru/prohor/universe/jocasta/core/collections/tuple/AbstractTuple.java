package ru.prohor.universe.jocasta.core.collections.tuple;

import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This file is a modified version of the original file developed by Stepan Koltsov at Yandex.
 * Original code is available under <a href="http://www.apache.org/licenses/LICENSE-2.0">the Apache License 2.0</a>
 * at <a href="https://github.com/v1ctor/bolts">github.com/v1ctor/bolts</a>
 */
public abstract class AbstractTuple {
    public final int valency;

    protected AbstractTuple(int valency) {
        this.valency = valency;
    }

    public abstract Object get(int n);

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (obj.getClass() != this.getClass())
            return false;

        AbstractTuple that = (AbstractTuple) obj;
        int valency = this.valency;
        for (int i = 1; i <= valency; ++i)
            if (!Objects.equals(this.get(i), that.get(i)))
                return false;
        return true;
    }


    @Override
    public int hashCode() {
        return IntStream.range(1, valency + 1).reduce(1, (i, next) -> 31 * i + Opt.ofNullable(next).orElse(0));
    }

    @Override
    public String toString() {
        return IntStream.range(1, valency + 1)
                .mapToObj(i -> get(i).toString())
                .collect(Collectors.joining(", ", "(", ")"));
    }
}

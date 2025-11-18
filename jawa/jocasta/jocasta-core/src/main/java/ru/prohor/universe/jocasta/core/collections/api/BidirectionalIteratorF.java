package ru.prohor.universe.jocasta.core.collections.api;

import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.core.functional.MonoPredicate;

import java.util.Objects;

public interface BidirectionalIteratorF<T> extends IteratorF<T> {
    boolean hasPrevious();

    T previous();

    @Override
    <B> BidirectionalIteratorF<B> map(MonoFunction<? super T, B> f);

    @Override
    BidirectionalIteratorF<T> filter(MonoPredicate<? super T> f);

    @Override
    default BidirectionalIteratorF<T> filterNot(MonoPredicate<? super T> f) {
        return filter(a -> !f.test(a));
    }

    @Override
    default BidirectionalIteratorF<T> filterNotNull() {
        return filter(Objects::nonNull);
    }
}

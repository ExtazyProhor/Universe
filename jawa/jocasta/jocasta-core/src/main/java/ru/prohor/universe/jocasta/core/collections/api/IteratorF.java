package ru.prohor.universe.jocasta.core.collections.api;

import jakarta.annotation.Nonnull;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.collections.impl.AbstractPrefetchingIterator;
import ru.prohor.universe.jocasta.core.collections.impl.EmptyIterator;
import ru.prohor.universe.jocasta.core.collections.impl.ImmutableDelegatedIterator;
import ru.prohor.universe.jocasta.core.collections.impl.ImmutableSingletonIterator;
import ru.prohor.universe.jocasta.core.collections.tuple.Tuple2;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.core.functional.MonoPredicate;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This file is a modified version of the original file developed by Stepan Koltsov at Yandex.
 * Original code is available under <a href="http://www.apache.org/licenses/LICENSE-2.0">the Apache License 2.0</a>
 * at <a href="https://github.com/v1ctor/bolts">github.com/v1ctor/bolts</a>
 */
public interface IteratorF<T> extends IterableF<T>, Iterator<T> {
    default void remove() {
        Iterator.super.remove();
    }

    @Nonnull
    @Override
    default IteratorF<T> iterator() {
        return this;
    }

    default <U, R> R collect(Collector<? super T, U, R> collector) {
        return stream().collect(collector);
    }

    @SuppressWarnings("unchecked")
    default <F> IteratorF<F> uncheckedCast() {
        return (IteratorF<F>) this;
    }

    @Override
    default Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    default ListF<T> toList() {
        if (!hasNext())
            return Cf.list();

        ArrayListF<T> result = new ArrayListF<>(minSize());
        forEachRemaining(result::add);
        return result.convertToReadOnly();
    }

    /*default SetF<T> toSet() {
        if (!hasNext()) return Cf.set();

        SetF<T> result = Cf.hashSet();
        forEachRemaining(result::add);
        return result.unmodifiable();
    }*/

    default <B> IteratorF<B> map(MonoFunction<? super T, B> f) {
        class MappedIterator implements IteratorF<B> {
            @Override
            public boolean hasNext() {
                return IteratorF.this.hasNext();
            }

            @Override
            public B next() {
                return f.apply(IteratorF.this.next());
            }

            @Override
            public void remove() {
                IteratorF.this.remove();
            }
        }
        return new MappedIterator();
    }

    default <B> IteratorF<B> flatMap(MonoFunction<? super T, ? extends Iterator<B>> f) {
        class FlatMappedIterator implements IteratorF<B> {
            private Iterator<B> cur = new EmptyIterator<>();

            @Override
            @SuppressWarnings("all")
            public boolean hasNext() {
                while (!cur.hasNext() && IteratorF.this.hasNext())
                    cur = f.apply(IteratorF.this.next());
                return cur.hasNext();
            }

            @Override
            public B next() {
                if (hasNext()) return cur.next();
                throw new NoSuchElementException("next on empty iterator");
            }
        }
        return new FlatMappedIterator();
    }

    default <B> IteratorF<B> flatMapL(MonoFunction<? super T, ? extends Iterable<B>> f) {
        return flatMap(e -> f.apply(e).iterator());
    }

    default <B> IteratorF<B> filterMap(MonoFunction<? super T, Opt<B>> f) {
        return flatMapL(f);
    }

    default IteratorF<T> filter(MonoPredicate<? super T> predicate) {
        class FilterIterator implements IteratorF<T> {
            private T next = null;
            private boolean nextIsPresent = false;

            @Override
            public boolean hasNext() {
                return nextIsPresent || fetch();
            }

            private boolean fetch() {
                T nextCandidate;
                while (IteratorF.this.hasNext()) {
                    nextCandidate = IteratorF.this.next();
                    if (predicate.test(nextCandidate)) {
                        this.next = nextCandidate;
                        nextIsPresent = true;
                        return true;
                    }
                }
                return false;
            }

            @Override
            public T next() {
                if (!nextIsPresent && !fetch())
                    throw new NoSuchElementException("Iterator is empty");
                nextIsPresent = false;
                return next;
            }
        }
        return new FilterIterator();
    }

    default IteratorF<T> filterNot(MonoPredicate<? super T> f) {
        return filter(a -> !f.test(a));
    }

    default IteratorF<T> filterNotNull() {
        return filter(Objects::nonNull);
    }

    default int count() {
        return count(b -> true);
    }

    default IteratorF<Tuple2<T, Integer>> zipWithIndex() {
        class ZippedIterator implements IteratorF<Tuple2<T, Integer>> {
            private int i = 0;

            public boolean hasNext() {
                return IteratorF.this.hasNext();
            }

            public Tuple2<T, Integer> next() {
                return new Tuple2<>(IteratorF.this.next(), i++);
            }

            public void remove() {
                IteratorF.this.remove();
            }
        }

        return new ZippedIterator();
    }

    default IteratorF<T> plus(Iterator<T> other) {
        return new IteratorF<T>() {
            final IteratorF<T> a = IteratorF.this;

            public boolean hasNext() {
                return a.hasNext() || other.hasNext();
            }

            public T next() {
                if (a.hasNext())
                    return a.next();
                else
                    return other.next();
            }
        };
    }

    default IteratorF<T> immutable() {
        return ImmutableDelegatedIterator.wrap(this);
    }

    default Opt<T> nextO() {
        return Opt.when(hasNext(), this::next);
    }

    default Optional<T> nextOptional() {
        return hasNext() ? Optional.of(next()) : Optional.empty();
    }

    default IteratorF<T> drop(int count) {
        while (count-- > 0 && hasNext())
            next();
        return this;
    }

    default IteratorF<T> take(int count) {
        class TakeIterator implements IteratorF<T> {
            private int left = count;

            public boolean hasNext() {
                return left > 0 && IteratorF.this.hasNext();
            }

            public T next() {
                if (left == 0)
                    throw new NoSuchElementException();
                T next = IteratorF.this.next();
                --left;
                return next;
            }
        }
        return new TakeIterator();
    }

    /*default ListF<T> takeFiltered(Function1B<? super T> filter, int count) {
        ListF<T> result = Cf.arrayList();
        while (result.size() < count && hasNext()) {
            T t = next();
            if (filter.apply(e)) {
                result.add(e);
            }
        }
        return result.makeReadOnly();
    }*/

    /*default ListF<T> takeSorted(int count) {
        return takeSorted(Comparator.naturalComparatorUnchecked(), count);
    }*/

    /*default ListF<T> takeSortedDesc(int count) {
        return takeSorted(Comparator.naturalComparatorUnchecked().reversed(), count);
    }*/
    
    /*default ListF<T> takeSorted(java.util.Comparator<? super T> comparator, int count) {
        if (count == 0) {
            return Cf.list();
        }
        if (count < 0) {
            throw new IllegalArgumentException("K must be greater than 0");
        }
        FixedSizeTop<T> top = FixedSizeTop.cons(count, (java.util.Comparator<T>) comparator);
        while (hasNext()) {
            top.add(next());
        }
        return top.getTopElements();
    }*/

    /*default ListF<T> takeSortedBy(MonoFunction<? super T, ?> f, int count) {
        return takeSorted(f.andThenNaturalComparator().nullLowC(), count);
    }*/
    
    default IteratorF<T> dropWhile(MonoPredicate<? super T> predicate) {
        while (hasNext()) {
            T t = next();
            if (!predicate.test(t))
                return new ImmutableSingletonIterator<>(t).plus(this);
        }
        return new EmptyIterator<>();
    }

    /*default ListF<T> takeSortedByDesc(MonoFunction<? super T, ?> f, int count) {
        return takeSorted(f.andThenNaturalComparator().nullLowC().reversed(), count);
    }*/

    default IteratorF<T> takeWhile(MonoPredicate<? super T> f) {
        class TakeWhileIterator extends AbstractPrefetchingIterator<T> {
            boolean end = false;

            @Override
            protected Opt<T> fetchNext() {
                if (!end && IteratorF.this.hasNext()) {
                    T t = IteratorF.this.next();
                    if (f.test(t))
                        return Opt.of(t);
                    else {
                        end = true;
                        return Opt.empty();
                    }
                }
                return Opt.empty();
            }
        }
        return new TakeWhileIterator();
    }

    /*default IteratorF<ListF<T>> paginate(int pageSize) {
        if (pageSize <= 0)
            throw new IllegalArgumentException();
        return new IteratorF<ListF<T>>() {
            @Override
            public boolean hasNext() {
                return IteratorF.this.hasNext();
            }

            @Override
            public ListF<T> next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                ArrayListF<T> items = new ArrayListF<>(pageSize);
                for (int i = 0; i < pageSize && IteratorF.this.hasNext(); ++i) {
                    items.add(IteratorF.this.next());
                }
                return items.convertToReadOnly();
            }

            @Override
            public int minSize() {
                return IteratorF.this.minSize() / pageSize;
            }
        };
    }*/

    /*default IteratorF<ListF<T>> paginateBy(BiPredicate<? super T, ? super T> startNewPagePredicate) {
        return new AbstractPrefetchingIterator<ListF<T>>() {

            private Opt<T> firstElement = Opt.empty();

            @Override
            protected Opt<ListF<T>> fetchNext() {
                if (!firstElement.isPresent()) {
                    firstElement = IteratorF.this.nextO();
                    if (!firstElement.isPresent()) {
                        return Opt.empty();
                    }
                }
                ListF<T> page = Cf.arrayList();
                E prev = firstElement.get();
                firstElement = Opt.empty();
                page.add(prev);
                while (IteratorF.this.hasNext()) {
                    E next = IteratorF.this.next();
                    if (startNewPagePredicate.test(prev, next)) {
                        firstElement = Opt.of(next);
                        break;
                    }
                    page.add(next);
                    prev = next;
                }
                return Opt.of(page);
            }

        };
    }*/

    /*default IteratorF<ListF<T>> paginateBy(MonoFunction<? super T, ?> f) {
        return paginateBy((a, b) -> !Objects.equals(f.apply(a), f.apply(b)));
    }*/
}

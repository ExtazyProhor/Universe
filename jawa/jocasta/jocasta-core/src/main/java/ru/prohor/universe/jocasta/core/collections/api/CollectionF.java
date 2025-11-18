package ru.prohor.universe.jocasta.core.collections.api;

import jakarta.annotation.Nonnull;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.collections.tuple.Tuple2;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.core.functional.MonoPredicate;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

public interface CollectionF<T> extends Collection<T>, IterableF<T> {
    @Override
    default boolean isEmpty() {
        return size() == 0;
    }

    default boolean isNotEmpty() {
        return !isEmpty();
    }

    default <F extends T> CollectionF<F> filterByType(Class<F> type) {
        return filter(type::isInstance).uncheckedCast();
    }

    @Nonnull
    @Override
    IteratorF<T> iterator();

    @Nonnull
    @Override
    default Stream<T> stream() {
        return Collection.super.stream();
    }

    default CollectionF<T> filter(MonoPredicate<? super T> predicate) {
        return iterator().filter(predicate).toList();
    }

    default CollectionF<T> filterNot(MonoPredicate<? super T> predicate) {
        return filter(predicate.negate());
    }

    default CollectionF<T> filterNotNull() {
        return filter(Objects::nonNull);
    }

    /*default Tuple2<? extends IterableF<T>, ? extends IterableF<T>> partition(MonoPredicate<? super T> predicate) {
        int size = size();
        ArrayListF<T> matched = new ArrayListF<>(size);
        ArrayListF<T> unmatched = new ArrayListF<>(size);
        forEach(t -> (predicate.test(t) ? matched : unmatched).add(t));
        if (matched.size() * 2 < size) {
            matched.trimToSize();
        }
        if (unmatched.size() * 2 < size) {
            unmatched.trimToSize();
        }
        return new Tuple2<>(matched.convertToReadOnly(), unmatched.convertToReadOnly());
    }*/

    /*default <B> ListF<B> map(MonoFunction<? super T, B> f) {
        if (isEmpty())
            return Cf.list();
        ArrayListF<B> res = new ArrayListF<>(size());
        for (T t : this) {
            res.add(f.apply(t));
        }
        return res.convertToReadOnly();
    }*/

    /*default <B> ListF<B> flatMap(Function<? super T, ? extends Collection<B>> f) {
        if (isEmpty()) return Cf.list();
        ListF<? extends Collection<B>> mapped = map(f);
        // precalculated size allow to avoid allocations
        int flatSize = 0;
        for (Collection<B> c : mapped) {
            flatSize += c.size();
        }
        ArrayListF<B> result = new ArrayListF<>(flatSize);
        for (Collection<B> c : mapped) {
            result.addAll(c);
        }
        return result.convertToReadOnly();
    }*/

    /*default <B> ListF<B> filterMap(MonoFunction<? super T, Opt<B>> f) {
        if (isEmpty()) return Cf.list();

        int size = size();
        ArrayListF<B> result = new ArrayListF<>(size);
        for (T t : this) {
            Option<B> option = f.apply(t);
            if (option.isPresent()) {
                result.add(option.get());
            }
        }
        if (result.size() * 2 < size) {
            result.trimToSize();
        }

        return result.convertToReadOnly();
    }*/

    /*default <B> ListF<B> filterMapOptional(Function<? super T, Optional<B>> f) {
        if (isEmpty()) return Cf.list();

        int size = size();
        ArrayListF<B> result = new ArrayListF<>(size);
        for (T t : this) {
            Optional<B> option = f.apply(t);
            if (option.isPresent()) {
                result.add(option.get());
            }
        }
        if (result.size() * 2 < size) {
            result.trimToSize();
        }
        return result.convertToReadOnly();
    }*/

    /*default <K, V> MapF<K, V> toMap(Function<? super T, K> fk, Function<? super T, V> fv) {
        if (isEmpty()) {
            return Cf.map();
        }
        // to keep compatibility with implementation with resizing
        int capacity = Math.max(16, Cf.initialCapacityForHashMapAndSet(size()));
        return toMap(Cf.x(new HashMap<>(capacity)), fk, fv);
    }

    default <K, V> MapF<K, V> toMap(MapF<K, V> result, Function<? super T, K> fk, Function<? super T, V> fv) {
        forEach(t -> result.put(fk.apply(t), fv.apply(t)));
        return result;
    }

    default <K> MapF<K, T> toMapMappingToKey(Function<? super T, K> mapper) {
        return toMap(mapper, t -> t);
    }

    default <V> MapF<T, V> toMapMappingToValue(Function<? super T, V> mapper) {
        return toMap(t -> t, mapper);
    }*/

    /*default <A, B> Tuple2List<A, B> toTuple2List(Function<? super T, ? extends A> fa, Function<? super T, ? extends B> fb) {
        return toTuple2List(Tuple2.join(fa.<T, A>uncheckedCast(), fb.<T, B>uncheckedCast()));
    }*/

    /*default <A, B> Tuple2List<A, B> toTuple2List(Function<? super T, Tuple2<A, B>> f) {
        return Cf.Tuple2List.cons(map(f));
    }*/

    /*default <A, B, C> Tuple3List<A, B, C> toTuple3List(
            Function<? super T, ? extends A> fa,
            Function<? super T, ? extends B> fb,
            Function<? super T, ? extends C> fc)
    {
        return toTuple3List(Tuple3.join(fa.<T, A>uncheckedCast(), fb.<T, B>uncheckedCast(), fc.<T, C>uncheckedCast()));
    }*/

    /*default <A, B, C> Tuple3List<A, B, C> toTuple3List(Function<? super T, Tuple3<A, B, C>> f) {
        return Cf.Tuple3List.cons(map(f));
    }*/

    @Override
    default void clear() {
        IteratorF<T> t = iterator();
        while (t.hasNext()) {
            t.next();
            t.remove();
        }
    }

    default String toStringImpl() {
        IteratorF<T> i = iterator();
        if (!i.hasNext())
            return "[]";
        return i.map(t -> t == this ? "(this Collection)" : t).join(", ", "[", "]");
    }

    default MonoPredicate<T> containsF() {
        return this::contains;
    }

    /*default ListF<T> toList() {
        return Cf.toList(this);
    }*/

    /*default SetF<T> unique() {
        return Cf.toSet(this);
    }*/

    /*default SetF<T> toSet() {
        return Cf.toSet(this);
    }*/

    /*default SetF<T> toLinkedHashSet() {
        return Cf.toLinkedHashSet(this);
    }

    default CollectionF<T> stableUnique() {
        return toList().stableUnique();
    }

    default CollectionF<T> stableUniqueBy(Function<? super T, ?> f) {
        return toList().stableUniqueBy(f);
    }*/

    /*@SuppressWarnings({"unchecked", "rawtypes"})
    default ListF<T> sorted() {
        if (size() <= 1) return toList();

        Object[] array = toArray();
        Arrays.sort(array);
        return new ReadOnlyArrayList<T>(array);
    }

    *//** Elements sorted by given comparator *//*
    default ListF<T> sorted(java.util.Comparator<? super T> comparator) {
        if (size() <= 1) return toList();

        T[] array = (T[]) toArray();
        Arrays.sort(array, comparator);
        return new ReadOnlyArrayList<>(array);
    }

    default ListF<T> sortedBy(Function<? super T, ?> f) {
        return sorted(f.andThenNaturalComparator().nullLowC());
    }

    default ListF<T> sortedByDesc(Function<? super T, ?> f) {
        return sorted(f.andThenNaturalComparator().nullLowC().reversed());
    }

    default ListF<T> takeFiltered(Function1B<? super T> filter, int k) {
        return iterator().takeFiltered(filter, Math.min(k, size()));
    }

    *//** First k elements sorted by {@link Comparator#naturalComparator()} *//*
    default ListF<T> takeSorted(int k) {
        return takeSorted(Comparator.naturalComparatorUnchecked(), k);
    }

    default ListF<T> takeSortedDesc(int k) {
        return takeSorted(Comparator.naturalComparatorUnchecked().reversed(), k);
    }

    *//** First k elements sorted by given comparator *//*
    default ListF<T> takeSorted(java.util.Comparator<? super T> comparator, int k) {
        if (k >= size()) {
            return sorted(comparator);
        } else {
            return iterator().takeSorted(comparator, k);
        }
    }

    default ListF<T> takeSortedBy(Function<? super T, ?> f, int k) {
        return takeSorted(f.andThenNaturalComparator().nullLowC(), k);
    }

    default ListF<T> takeSortedByDesc(Function<? super T, ?> f, int k) {
        return takeSorted(f.andThenNaturalComparator().nullLowC().reversed(), k);
    }

    default ListF<T> takeSorted(int offset, int length) {
        return takeSorted(Comparator.naturalComparatorUnchecked(), offset, length);
    }

    default ListF<T> takeSorted(java.util.Comparator<? super T> comparator, int offset, int length) {
        int size = size();
        if (offset < 0 || offset > size) {
            throw new IndexOutOfBoundsException("Incorrect offset: " + offset);
        }
        if (length < 0) {
            throw new IllegalArgumentException("Length(" + length + ") must be >= 0");
        }
        if (length == 0 || offset == size) {
            return Cf.list();
        }
        if (offset == 0) {
            return takeSorted(comparator, length);
        }
        T[] elements = (T[]) toArray();
        NthElement.inplaceNth(elements, comparator, offset);
        FixedSizeTop<T> top = FixedSizeTop.cons(Math.min(size - offset, length),
                (java.util.Comparator<T>) comparator);

        for (int i = offset; i < elements.length; ++i) {
            top.add(elements[i]);
        }
        return top.getTopElements();
    }

    default ListF<T> takeSortedBy(Function<? super T, ?> f, int offset, int length) {
        return takeSorted(f.andThenNaturalComparator().nullLowC(), offset, length);
    }

    default ListF<T> takeSortedByDesc(Function<? super T, ?> f, int offset, int length) {
        return takeSorted(f.andThenNaturalComparator().nullLowC().reversed(), offset, length);
    }*/

    /*default T getSorted(int n) {
        return getSorted(ComparatorF.naturalComparator().uncheckedCastC(), n);
    }

    default T getSorted(java.util.Comparator<? super T> comparator, int n) {
        if (n < 0 || n >= size()) {
            throw new IndexOutOfBoundsException(String.format("Index: %d, Size: %d", n, size()));
        }
        T[] elements = (T[]) toArray();
        NthElement.inplaceNth(elements, comparator, elements.length, n);
        return elements[n];
    }

    default T getSorted(Function<? super T, ?> f, int n) {
        return getSorted(f.andThenNaturalComparator().nullLowC(), n);
    }*/

    /*default ListF<T> shuffle() {
        ListF<T> r = Cf.toArrayList(this);
        Collections.shuffle(r);
        return r.makeReadOnly();
    }*/

    /*default <V> MapF<V, ListF<T>> groupBy(Function<? super T, ? extends V> m) {
        return groupByMapValues(m, t -> t);
    }*/

    /*default <K, V> MapF<K, ListF<V>> groupByMapValues(
            Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper)
    {
        if (isEmpty()) return Cf.map();

        Function0<ListF<V>> newArrayListF = ArrayListF::new;

        MapF<K, ListF<V>> map = Cf.hashMap();
        for (T t : this) {
            K key = keyMapper.apply(t);
            ListF<V> list = map.getOrElseUpdate(key, newArrayListF);
            list.add(valueMapper.apply(t));
        }
        return map;
    }*/

    /*default <B> Tuple2List<T, B> zipWithFlatMapO(Function<? super T, Option<B>> f) {
        Tuple2List<T, B> result = Tuple2List.arrayList();
        for (T t : this) {
            Option<B> option = f.apply(t);
            if (option.isPresent()) {
                result.add(Tuple2.tuple(t, option.get()));
            }
        }
        return result;
    }

    default <B> Tuple2List<T, B> zipWithFlatMapOptional(Function<? super T, Optional<B>> f) {
        Tuple2List<T, B> result = Tuple2List.arrayList();
        for (T t : this) {
            Optional<B> option = f.apply(t);
            if (option.isPresent()) {
                result.add(Tuple2.tuple(t, option.get()));
            }
        }
        return result;
    }

    default <B> Tuple2List<T, B> zipWithFlatMap(Function<? super T, ? extends Collection<B>> f) {
        Tuple2List<T, B> result = Tuple2List.arrayList();
        for (T t : this) {
            for (B b : f.apply(t)) {
                result.add(t, b);
            }
        }
        return result.makeReadOnly();
    }*/

    /*default CollectionF<T> plus1(T t) {
        return Cf.toList(this).plus1(t);
    }

    *//**
     * Collection with all elements of this collection and that collection.
     *//*
    default CollectionF<T> plus(Collection<? extends T> elements) {
        return plus(elements.iterator());
    }

    *//**
     * Collection with all elements of this collection and that collection.
     *//*
    @SuppressWarnings("unchecked")
    default CollectionF<T> plus(Iterator<? extends T> iterator) {
        if (!iterator.hasNext()) return this;

        if (isEmpty()) return (ListF<T>) Cf.x(iterator).toList();

        ListF<T> c = Cf.arrayList();
        c.addAll(this);
        iterator.forEachRemaining(c::add);
        return c;
    }

    *//**
     * Varargs variant of {@link #plus(Collection)}.
     *//*
    @SuppressWarnings("unchecked")
    default CollectionF<T> plus(T... additions) {
        return plus(Cf.list(additions));
    }


    *//**
     * Varargs version of <code>addAll</code>
     *
     * @see #addAll(Collection)
     *//*
    @SuppressWarnings("unchecked")
    default void addAll(T... additions) {
        addAll(Cf.list(additions));
    }*/

    @SuppressWarnings("unchecked")
    default T[] toArray(Class<T> type) {
        return toArray((T[]) Array.newInstance(type, size()));
    }

    static <X, A, T> A toPrimitiveArrayImpl(
            CollectionF<X> collection,
            A array,
            MonoFunction<X, T> mapper
    ) {
        int i = 0;
        for (X t : collection)
            Array.set(array, i++, mapper.apply(t));
        if (i != collection.size())
            throw new IllegalStateException();
        return array;
    }

    default byte[] mapToByteArray(MonoFunction<T, Byte> mapper) {
        return toPrimitiveArrayImpl(this, new byte[size()], mapper);
    }

    default short[] mapToShortArray(MonoFunction<T, Short> mapper) {
        return toPrimitiveArrayImpl(this, new short[size()], mapper);
    }

    default int[] mapToIntArray(MonoFunction<T, Integer> mapper) {
        return toPrimitiveArrayImpl(this, new int[size()], mapper);
    }

    default long[] mapToLongArray(MonoFunction<T, Long> mapper) {
        return toPrimitiveArrayImpl(this, new long[size()], mapper);
    }

    default boolean[] mapToBooleanArray(MonoFunction<T, Boolean> mapper) {
        return toPrimitiveArrayImpl(this, new boolean[size()], mapper);
    }

    default char[] mapToCharArray(MonoFunction<T, Character> mapper) {
        return toPrimitiveArrayImpl(this, new char[size()], mapper);
    }

    default float[] mapToFloatArray(MonoFunction<T, Float> mapper) {
        return toPrimitiveArrayImpl(this, new float[size()], mapper);
    }

    default double[] mapToDoubleArray(MonoFunction<T, Double> mapper) {
        return toPrimitiveArrayImpl(this, new double[size()], mapper);
    }

    CollectionF<T> immutable();

    @SuppressWarnings("unchecked")
    default <F> CollectionF<F> uncheckedCast() {
        return (CollectionF<F>) this;
    }

    default <F> CollectionF<F> cast(Class<F> type) {
        for (T item : this)
            type.cast(item);
        return uncheckedCast();
    }

    /*default ListF<ListF<T>> paginate(int pageSize) {
        return iterator().paginate(pageSize).toList();
    }

    default ListF<ListF<T>> paginateBy(BiPredicate<T, T> startNewPage) {
        return iterator().paginateBy(startNewPage).toList();
    }

    default ListF<ListF<T>> paginateBy(Function<T, ?> f) {
        return iterator().paginateBy(f).toList();
    }*/

    /*@SuppressWarnings("unchecked")
    default <F> ListF<F> flatten() {
        return Cf.flatten(this.cast());
    }*/

    @Override
    default boolean remove(Object o) {
        IteratorF<T> t = iterator();
        if (o == null) {
            while (t.hasNext()) {
                if (t.next() == null) {
                    t.remove();
                    return true;
                }
            }
        } else {
            while (t.hasNext()) {
                if (o.equals(t.next())) {
                    t.remove();
                    return true;
                }
            }
        }
        return false;
    }

    default boolean removeTs(T t) {
        return remove(t);
    }

    @Override
    default boolean contains(Object o) {
        IteratorF<T> t = iterator();
        if (o == null) {
            while (t.hasNext()) {
                if (t.next() == null)
                    return true;
            }
        } else {
            while (t.hasNext()) {
                if (o.equals(t.next()))
                    return true;
            }
        }
        return false;
    }

    default boolean containsTs(T t) {
        return contains(t);
    }

    @Override
    default boolean add(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o))
                return false;
        }
        return true;
    }

    default boolean containsAllTs(Collection<? extends T> coll) {
        return containsAll(coll);
    }

    @Override
    default boolean addAll(Collection<? extends T> c) {
        boolean modified = false;
        for (T value : c)
            if (add(value))
                modified = true;
        return modified;
    }

    @Override
    default boolean removeAll(@Nonnull Collection<?> c) {
        boolean modified = false;
        Iterator<T> iterator = iterator();
        while (iterator.hasNext()) {
            if (c.contains(iterator.next())) {
                iterator.remove();
                modified = true;
            }
        }
        return modified;
    }

    default boolean removeAllTs(Collection<? extends T> c) {
        return removeAll(c);
    }

    @Override
    default boolean retainAll(@Nonnull Collection<?> c) {
        boolean modified = false;
        Iterator<T> t = iterator();
        while (t.hasNext()) {
            if (!c.contains(t.next())) {
                t.remove();
                modified = true;
            }
        }
        return modified;
    }

    default boolean retainAllTs(Collection<? extends T> c) {
        return retainAll(c);
    }

    /*default MapF<T, Integer> countBy() {
        return countBy(t -> t);
    }

    default <F> MapF<F, Integer> countBy(MonoFunction<? super T, ? extends F> function) {
        MapF<F, Integer> result = Cf.hashMap();
        for (T t : this) {
            F f = function.apply(t);
            result.put(f, result.getOrElse(f, 0) + 1);
        }
        return result.unmodifiable();
    }*/


    @Nonnull
    @Override
    default Object[] toArray() {
        Object[] r = new Object[size()];
        Iterator<T> it = iterator();
        for (int i = 0; i < r.length; i++) {
            if (!it.hasNext())
                return Arrays.copyOf(r, i);
            r[i] = it.next();
        }
        return it.hasNext() ? finishToArray(r, it) : r;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    default <R> R[] toArray(R[] a) {
        int size = size();
        R[] r = a.length >= size ? a : (R[]) Array.newInstance(a.getClass().getComponentType(), size);
        Iterator<T> it = iterator();

        for (int i = 0; i < r.length; i++) {
            if (!it.hasNext()) {
                if (a != r)
                    return Arrays.copyOf(r, i);
                r[i] = null;
                return r;
            }
            r[i] = (R) it.next();
        }
        return it.hasNext() ? finishToArray(r, it) : r;
    }

    @SuppressWarnings("unchecked")
    static <T> T[] finishToArray(T[] r, Iterator<?> it) {
        int i = r.length;
        while (it.hasNext()) {
            int cap = r.length;
            if (i == cap) {
                int newCap = ((cap / 2) + 1) * 3;
                if (newCap <= cap) { // integer overflow
                    if (cap == Integer.MAX_VALUE)
                        throw new OutOfMemoryError("Required array size too large");
                    newCap = Integer.MAX_VALUE;
                }
                r = Arrays.copyOf(r, newCap);
            }
            r[i++] = (T) it.next();
        }
        // trim if overallocated
        return (i == r.length) ? r : Arrays.copyOf(r, i);
    }
}

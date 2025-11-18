package ru.prohor.universe.jocasta.core.collections.api;

import jakarta.annotation.Nonnull;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.collections.impl.ReverseIterator;
import ru.prohor.universe.jocasta.core.collections.tuple.Tuple2;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.core.functional.MonoPredicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * This file is a modified version of the original file developed by Stepan Koltsov at Yandex.
 * Original code is available under <a href="http://www.apache.org/licenses/LICENSE-2.0">the Apache License 2.0</a>
 * at <a href="https://github.com/v1ctor/bolts">github.com/v1ctor/bolts</a>
 */
public interface ListF<T> extends CollectionF<T>, List<T> {
    @Override
    default boolean isEmpty() {
        return CollectionF.super.isEmpty();
    }

    default Opt<T> getO(int index) {
        return Opt.when(index >= 0 && index < size(), () -> get(index));
    }

    @Nonnull
    @Override
    default Object[] toArray() {
        return CollectionF.super.toArray();
    }

    @Nonnull
    @Override
    default <R> R[] toArray(@Nonnull R[] a) {
        return CollectionF.super.toArray(a);
    }

    @Nonnull
    @Override
    IteratorF<T> iterator();

    default IteratorF<T> reverseIterator() {
        return new ReverseIterator<>(listIterator(size()));
    }

    @Override
    default <F extends T> ListF<F> filterByType(Class<F> type) {
        return filter(type::isInstance).uncheckedCast();
    }

    /**
     * Return pair of lists, first list contains elements matching <code>p</code>
     * and second lists contains elements matching <code>!p</code>.
     */
    @Override
    default Tuple2<ListF<T>, ListF<T>> partition(MonoPredicate<? super T> p) {
        return (Tuple2<ListF<T>, ListF<T>>) CollectionF.super.partition(p);
    }

    @Override
    default ListF<T> stableUnique() {
        return stableUniqueBy(t -> t);
    }

    @Override
    default ListF<T> stableUniqueBy(Function<? super T, ?> f) {
        if (size() <= 1) {
            return this;
        }

        SetF<Object> added = Cf.hashSet();
        return filter(t -> added.add(f.apply(t)));
    }

    /**
     * Removes the objects in the specified range from the start to the, but not
     * including, end index.
     *
     *
     * @param start
     *            the index at which to start removing
     * @param end
     *            the index one past the end of the range to remove
     *
     * @exception UnsupportedOperationException
     *                when removing from this List is not supported
     * @exception IndexOutOfBoundsException
     *                when <code>start < 0
     */
    /* protected */ default void removeRangeProtected(int start, int end) {
        Iterator<?> it = listIterator(start);
        for (int i = start; i < end; i++) {
            it.next();
            it.remove();
        }
    }

    /**
     * Removes the object at the specified location from this List.
     *
     *
     * @param location
     *            the index of the object to remove
     * @return the removed object
     *
     * @exception UnsupportedOperationException
     *                when removing from this List is not supported
     * @exception IndexOutOfBoundsException
     *                when <code>location < 0 || >= size()</code>
     */
    default T remove(int location) {
        throw new UnsupportedOperationException();
    }

    /**
     * Replaces the element at the specified location in this List with the
     * specified object.
     *
     *
     * @param location
     *            the index at which to put the specified object
     * @param object
     *            the object to add
     * @return the previous element at the index
     *
     * @exception UnsupportedOperationException
     *                when replacing elements in this List is not supported
     * @exception ClassCastException
     *                when the class of an object is inappropriate for this List
     * @exception IllegalArgumentException
     *                when an object cannot be added to this List
     * @exception IndexOutOfBoundsException
     *                when <code>location < 0 || >= size()</code>
     */
    default T set(int location, T object) {
        throw new UnsupportedOperationException();
    }

    /** Sub list from index to index */
    ListF<T> subList(int fromIndex, int toIndex);

    /** Concatenate two lists */
    default ListF<T> plus(List<? extends T> addition) {
        if (addition.isEmpty()) {
            return this;
        } else if (isEmpty()) {
            return Cf.x(addition).uncheckedCast();
        } else {
            ListF<T> result = Cf.arrayListWithCapacity(size() + addition.size());
            result.addAll(this);
            result.addAll(addition);
            return result.makeReadOnly();
        }
    }

    @Override
    default ListF<T> plus(Collection<? extends T> elements) {
        if (elements.isEmpty()) {
            return this;
        } else {
            ListF<T> result = Cf.arrayListWithCapacity(size() + elements.size());
            result.addAll(this);
            result.addAll(elements);
            return result.makeReadOnly();
        }
    }

    @Override
    default ListF<T> plus(Iterator<? extends T> iterator) {
        return (ListF<T>) CollectionF.super.plus(iterator);
    }

    @SuppressWarnings("unchecked")
    default ListF<T> plus(T... additions) {
        return plus(Cf.list(additions));
    }

    @Override
    default ListF<T> plus1(T t) {
        if (isEmpty()) return Cf.list(t);

        ListF<T> c = Cf.arrayListWithCapacity(size() + 1);
        c.addAll(this);
        c.add(t);
        return c.makeReadOnly();
    }

    default T first() {
        return get(0);
    }

    default T last() {
        return get(size() - 1);
    }

    default Option<T> firstO() {
        return iterator().nextO();
    }

    default Option<T> lastO() {
        return reverseIterator().nextO();
    }

    /** Task first <code>count</code> elements */
    default ListF<T> take(int count) {
        if (count <= 0) return Cf.list();
        else if (count < size()) return subList(0, count);
        else return this;
    }

    /** Drop first count elements */
    default ListF<T> drop(int count) {
        if (count <= 0) return this;
        else if (count < size()) return subList(count, size());
        else return Cf.list();
    }


    default ListF<T> rtake(int count) {
        return drop(length() - count);
    }

    default ListF<T> rdrop(int count) {
        return take(length() - count);
    }

    /** Longest prefix of elements that satisfy p */
    default ListF<T> takeWhile(MonoPredicate<? super T> f) {
        return iterator().takeWhile(f).toList();
    }

    /** Elements after {@link #takeWhile(MonoPredicate)} */
    default ListF<T> dropWhile(MonoPredicate<? super T> f) {
        return iterator().dropWhile(f).toList();
    }

    /** Pair of sublists returned by {@link #takeWhile(MonoPredicate)} and {@link #dropWhile(MonoPredicate)} */
    //ListF<T> span(MonoPredicate<? super T> p);


    /** Fold right */
    default <B> B foldRight(B z, Function2<? super T, ? super B, B> f) {
        return reverseIterator().foldLeft(z, f.swap());
    }

    /** Reduce right */
    default T reduceRight(BiFunction<T, T, T> f) {
        return reverseIterator().reduceLeft((a, b) -> f.apply(b, a));
    }

    default Option<T> reduceRightO(BiFunction<T, T, T> f) {
        return reverseIterator().reduceLeftO((a, b) -> f.apply(b, a));
    }


    @Override
    default ListF<T> toList() {
        return this;
    }

    /**
     * Return unmodifiable list with content of this list.
     * This list becomes invalid after method invocation.
     *
     * @see ArrayListF#convertToReadOnly()
     */
    default ListF<T> makeReadOnly() {
        return Cf.toList(this);
    }

    @Override
    default ListF<T> immutable() {
        //if (this instanceof Unmodifiable) return this;
        return UnmodifiableDefaultListF.wrap(this);
    }

    default int length() {
        return size();
    }

    /**
     * List with elements in reverse order
     */
    default ListF<T> reverse() {
        if (size() <= 1) return this;
        ArrayListF<T> res = new ArrayListF<>(size());
        reverseIterator().forEachRemaining(res::add);
        return res.convertToReadOnly();
    }

    @Override
    default <F> ListF<F> uncheckedCast() {
        return cast();
    }

    @Override
    default <F> ListF<F> cast() {
        return (ListF<F>) CollectionF.super.<F>cast();
    }

    @Override
    default <F> ListF<F> cast(Class<F> type) {
        return (ListF<F>) CollectionF.super.cast(type);
    }

    default <B> Tuple2List<T, B> zip(ListF<? extends B> that) {
        return Tuple2List.zip(this, that);
    }

    default <B> Tuple2List<T, B> zipWith(Function<? super T, ? extends B> f) {
        ListF<? extends B> that = map(f);
        return zip(that);
    }

    default Tuple2List<T, Integer> zipWithIndex() {
        ArrayList<Tuple2<T, Integer>> res = new ArrayList<>(size());
        iterator().zipWithIndex().forEachRemaining(res::add);
        return new Tuple2List<>(res);
    }

    /** @deprecated */
    @Override
    default boolean remove(Object o) {
        return CollectionF.super.remove(o);
    }

    /** @deprecated */
    @Override
    default boolean removeAll(Collection<?> c) {
        return CollectionF.super.removeAll(c);
    }

    /** @deprecated */
    @Override
    default boolean contains(Object o) {
        return CollectionF.super.contains(o);
    }

    /** @deprecated */
    @Override
    default boolean containsAll(Collection<?> coll) {
        return CollectionF.super.containsAll(coll);
    }

    default boolean startsWith(List<? super T> prefix) {
        return this.take(prefix.size()).equals(prefix);
    }

    /** @deprecated */
    @Override
    default boolean retainAll(Collection<?> c) {
        return CollectionF.super.retainAll(c);
    }

    /**
     * Adds the specified object at the end of this List.
     *
     *
     * @param object
     *            the object to add
     * @return true
     *
     * @exception UnsupportedOperationException
     *                when adding to this List is not supported
     * @exception ClassCastException
     *                when the class of the object is inappropriate for this
     *                List
     * @exception IllegalArgumentException
     *                when the object cannot be added to this List
     */
    @Override
    default boolean add(T object) {
        add(size(), object);
        return true;
    }

    /**
     * Inserts the objects in the specified Collection at the specified location
     * in this List. The objects are added in the order they are returned from
     * the Collection iterator.
     *
     *
     * @param location
     *            the index at which to insert
     * @param collection
     *            the Collection of objects
     * @return true if this List is modified, false otherwise
     *
     * @exception UnsupportedOperationException
     *                when adding to this List is not supported
     * @exception ClassCastException
     *                when the class of an object is inappropriate for this List
     * @exception IllegalArgumentException
     *                when an object cannot be added to this List
     * @exception IndexOutOfBoundsException
     *                when <code>location < 0 || >= size()</code>
     */
    default boolean addAll(int location, Collection<? extends T> collection) {
        Iterator<? extends T> it = collection.iterator();
        while (it.hasNext()) {
            add(location++, it.next());
        }
        return !collection.isEmpty();
    }

    @Override
    default boolean addAll(Collection<? extends T> c) {
        return addAll(size(), c);
    }

    /**
     * Removes all elements from this List, leaving it empty.
     *
     *
     * @exception UnsupportedOperationException
     *                when removing from this List is not supported
     *
     * @see List#isEmpty
     * @see List#size
     */
    @Override
    default void clear() {
        removeRangeProtected(0, size());
    }

    /**
     * Inserts the specified object into this List at the specified location.
     * The object is inserted before any previous element at the specified
     * location. If the location is equal to the size of this List, the object
     * is added at the end.
     *
     *
     * @param location
     *            the index at which to insert
     * @param object
     *            the object to add
     *
     * @exception UnsupportedOperationException
     *                when adding to this List is not supported
     * @exception ClassCastException
     *                when the class of the object is inappropriate for this
     *                List
     * @exception IllegalArgumentException
     *                when the object cannot be added to this List
     * @exception IndexOutOfBoundsException
     *                when <code>location < 0 || >= size()</code>
     */
    default void add(int location, T object) {
        throw new UnsupportedOperationException();
    }

    /**
     * Searches this List for the specified object and returns the index of the
     * first occurrence.
     *
     *
     * @param object
     *            the object to search for
     * @return the index of the first occurrence of the object
     * @deprecated
     */
    default int indexOf(Object object) {
        ListIterator<?> it = listIterator();
        if (object != null) {
            while (it.hasNext()) {
                if (object.equals(it.next())) {
                    return it.previousIndex();
                }
            }
        } else {
            while (it.hasNext()) {
                if (it.next() == null) {
                    return it.previousIndex();
                }
            }
        }
        return -1;
    }

    default int indexOfTs(T o) {
        return indexOf(o);
    }

    default int indexOfTu(Object o) {
        return indexOf(o);
    }

    default boolean isSorted() {
        return this.isSorted(ComparatorF.naturalComparatorUnchecked());
    }

    default boolean isSorted(Comparator<? super T> comparator) {
        IteratorF<T> it = iterator();

        if (!it.hasNext()) {
            return true;
        }

        T t = it.next();
        while (it.hasNext()) {
            T next = it.next();
            if (comparator.compare(t, next) > 0) {
                return false;
            }
            t = next;
        }
        return true;
    }

    default <U extends Comparable<U>> boolean isSortedBy(MonoFunction<? super T, U> f) {
        return isSorted(Comparator.comparing(f));
    }

    /*default Vec2<T> match2() {
        if (size() != 2) {
            throw new IllegalStateException();
        }
        return new Vec2<>(get(0), get(1));
    }

    default Vec3<T> match3() {
        if (size() != 3) {
            throw new IllegalStateException();
        }
        return new Vec3<>(get(0), get(1), get(2));
    }

    default Vec4<T> match4() {
        if (size() != 4) {
            throw new IllegalStateException();
        }
        return new Vec4<>(get(0), get(1), get(2), get(3));
    }

    default Vec5<T> match5() {
        if (size() != 5) {
            throw new IllegalStateException();
        }
        return new Vec5<>(get(0), get(1), get(2), get(3), get(4));
    }

    default Vec6<T> match6() {
        if (size() != 6) {
            throw new IllegalStateException();
        }
        return new Vec6<>(get(0), get(1), get(2), get(3), get(4), get(5));
    }

    default Vec7<T> match7() {
        if (size() != 7) {
            throw new IllegalStateException();
        }
        return new Vec7<>(get(0), get(1), get(2), get(3), get(4), get(5), get(6));
    }

    default Vec8<T> match8() {
        if (size() != 8) {
            throw new IllegalStateException();
        }
        return new Vec8<>(get(0), get(1), get(2), get(3), get(4), get(5), get(6), get(7));
    }*/
}

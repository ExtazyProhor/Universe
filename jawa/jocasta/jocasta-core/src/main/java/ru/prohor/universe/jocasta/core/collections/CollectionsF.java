package ru.prohor.universe.jocasta.core.collections;

import ru.prohor.universe.jocasta.core.collections.api.CollectionF;
import ru.prohor.universe.jocasta.core.collections.api.IteratorF;
import ru.prohor.universe.jocasta.core.collections.api.ListF;
import ru.prohor.universe.jocasta.core.collections.api.MapF;
import ru.prohor.universe.jocasta.core.collections.api.SetF;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.DiFunction;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * This file is a modified version of the original file developed by Stepan Koltsov at Yandex.
 * Original code is available under <a href="http://www.apache.org/licenses/LICENSE-2.0">the Apache License 2.0</a>
 * at <a href="https://github.com/v1ctor/bolts">github.com/v1ctor/bolts</a>
 */
public final class CollectionsF {
    private CollectionsF() {}

    public static <T> ListF<T> wrap(List<T> list) {
        return x(list);
    }

    /**
     * Wrap Set.
     *
     * @see #x(Set)
     */
    public static <T> SetF<T> wrap(Set<T> set) {
        return x(set);
    }

    /**
     * Wrap Collection.
     *
     * @see #x(Collection)
     */
    public static <T> CollectionF<T> wrap(Collection<T> coll) {
        return x(coll);
    }

    /**
     * Wrap Iterator.
     *
     * @see #x(Iterator)
     */
    public static <T> IteratorF<T> wrap(Iterator<T> iter) {
        return x(iter);
    }

    /**
     * Wrap Enumeration.
     *
     * @see #x(java.util.Enumeration)
     */
    public static <T> IteratorF<T> wrap(java.util.Enumeration<T> enumeration) {
        return x(enumeration);
    }

    /**
     * Wrap Map.
     *
     * @see #x(Map)
     */
    public static <K, V> MapF<K, V> wrap(Map<K, V> map) {
        return x(map);
    }


    /**
     * Wrap Properties.
     *
     * @see #x(Properties)
     */
    public static MapF<String, String> wrap(Properties ps) {
        return x(ps);
    }

    /**
     * Wrap array.
     *
     * @see #x(Object[])
     */
    public static <T> ListF<T> wrap(T[] array) {
        return x(array);
    }

    /** Wrap iterator */
    public static <T> IteratorF<T> x(Iterator<T> iterator) {
        return DefaultIteratorF.wrap(iterator);
    }

    /** Wrap enumeration */
    public static <T> IteratorF<T> x(java.util.Enumeration<T> enumeration) {
        return DefaultEnumerationF.wrap(enumeration);
    }

    /** Wrap collection */
    public static <T> CollectionF<T> x(Collection<T> collection) {
        if (collection instanceof List<?>)
            return DefaultListF.wrap((List<T>) collection);
        else if (collection instanceof Set<?>)
            return DefaultSetF.wrap((Set<T>) collection);
        else
            return DefaultCollectionF.wrap(collection);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> Opt<T> x(Optional<T> optional) {
        return Opt.wrap(optional);
    }

    /** Wrap list */
    public static <T> ListF<T> x(List<T> list) {
        return DefaultListF.wrap(list);
    }

    /** Wrap set */
    public static <T> SetF<T> x(Set<T> set) {
        return DefaultSetF.wrap(set);
    }

    /** Wrap map */
    public static <K, V> MapF<K, V> x(Map<K, V> map) {
        return DefaultMapF.wrap(map);
    }

    /** Wrap properties as set of String, String pairs */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static MapF<String, String> x(Properties properties) {
        return x((Map) properties);
    }

    /**
     * Wrap array.
     *
     * @see Arrays#asList(Object...)
     * @see #list(Object...)
     */
    public static <T> ListF<T> x(T[] array) {
        return x(Arrays.asList(array));
    }


    /** Empty set */
    @SuppressWarnings({"unchecked"})
    public static <T> SetF<T> set() {
        return EmptySet.INSTANCE;
    }

    /** Create singleton set */
    public static <T> SetF<T> set(T o) {
        return new SingletonSet<>(o);
    }

    /**
     * Create set of specified elements.
     *
     * @return set of either 1 or 2 elements
     */
    public static <T> SetF<T> set(T e1, T e2) {
        return set(e1).plus1(e2);
    }

    /**
     * Create set of specified elements.
     *
     * @return set of either 1, 2 or 3 elements
     */
    public static <T> SetF<T> set(T e1, T e2, T e3) {
        return set(e1, e2).plus1(e3);
    }

    /** Create set of specified elements */
    @SuppressWarnings("unchecked")
    public static <T> SetF<T> set(T... elements) {
        if (elements.length == 0) return set();
        else if (elements.length == 1) return set(elements[0]);
        else if (elements.length == 2) return set(elements[0], elements[1]);
        else if (elements.length == 3) return set(elements[0], elements[1], elements[2]);
        else return hashSet(elements);
    }

    /** @deprecated inline */
    public static <T> SetF<T> set(Collection<T> elements) {
        return toSet(elements);
    }

    /**
     * Create set of elements from collection.
     */
    public static <T> SetF<T> toSet(Collection<T> elements) {
        if (elements.isEmpty()) return set();
            //else if (elements.size() == 1) return singleton(elements.iterator().next());
        else return toHashSet(elements).unmodifiable();
    }

    /**
     * Create mutable hash set.
     */
    public static <A> SetF<A> hashSet() {
        return x(new HashSet<A>());
    }

    public static <A> SetF<A> hashSetWithExpectedSize(int expectedSize) {
        return x(new HashSet<>(initialCapacityForHashMapAndSet(expectedSize)));
    }

    /** @deprecated inline */
    public static <T> SetF<T> hashSet(Collection<T> collection) {
        return toHashSet(collection);
    }

    /**
     * Create mutable hash set of specified elements.
     */
    public static <T> SetF<T> toHashSet(Collection<T> collection) {
        return x(new HashSet<>(collection));
    }

    /**
     * Create mutable hash set of specified elements.
     */
    @SuppressWarnings("unchecked")
    public static <T> SetF<T> hashSet(T... elements) {
        return toHashSet(list(elements));
    }

    public static <T> Function0<SetF<T>> newHashSetF() {
        return Cf::hashSet;
    }

    /**
     * Create mutable identity hash set.
     */
    public static <T> SetF<T> identityHashSet() {
        return new SetFromMap<>(new IdentityHashMap<T, Boolean>());
    }

    /** @deprecated inline */
    public static <T> SetF<T> identityHashSet(Collection<T> elements) {
        return toIdentityHashSet(elements);
    }

    /**
     * Create mutable identity hash set with specified elements.
     */
    public static <T> SetF<T> toIdentityHashSet(Collection<T> elements) {
        SetF<T> set = identityHashSet();
        set.addAll(elements);
        return set;
    }

    /**
     * Create mutable identity hash set with specified elements.
     */
    @SuppressWarnings("unchecked")
    public static <T> SetF<T> identityHashSet(T... elements) {
        return toIdentityHashSet(list(elements));
    }

    /**
     * Create extended tree set.
     */
    public static <A> SetF<A> treeSet() {
        return x(new TreeSet<>());
    }

    /** @deprecated inline */
    public static <T> SetF<T> treeSet(Collection<T> collection) {
        return toTreeSet(collection);
    }

    /**
     * Create tree set of specified elements.
     */
    public static <T> SetF<T> toTreeSet(Collection<T> collection) {
        return x(new TreeSet<>(collection));
    }

    /**
     * Create tree set of specified elements.
     */
    @SuppressWarnings("unchecked")
    public static <T> SetF<T> treeSet(T... elements) {
        return toTreeSet(list(elements));
    }

    /**
     * Empty immutable list.
     */
    public static <T> ListF<T> list() {
        return List.cons();
    }

    /**
     * Immutable singleton list.
     */
    public static <T> ListF<T> list(T t) {
        return List.cons(t);
    }

    /**
     * Immutable list with two elements.
     */
    public static <T> ListF<T> list(T e1, T e2) {
        return List.cons(e1, e2);
    }

    /**
     * Create list of specified elements.
     *
     * The resulting list is immutable.
     *
     * @see #wrap(Object[]) for real array wrapping
     */
    @SafeVarargs
    public static <T> ListF<T> list(T... elements) {
        return List.cons(elements);
    }

    /** @deprecated inline */
    public static <T> ListF<T> list(Collection<T> elements) {
        return toList(elements);
    }

    /**
     * Create list of elements from given collection.
     */
    public static <T> ListF<T> toList(Collection<T> elements) {
        return List.cons(elements);
    }

    public static ListF<Integer> intList(int... elements) {
        return IntegerArray.arrayToList(elements);
    }

    public static ListF<Long> longList(long... elements) {
        return LongArray.arrayToList(elements);
    }

    public static ListF<Short> shortList(short... elements) {
        return ShortArray.arrayToList(elements);
    }

    public static ListF<Character> charList(char... elements) {
        return CharacterArray.arrayToList(elements);
    }

    public static ListF<Byte> byteList(byte... elements) {
        return ByteArray.arrayToList(elements);
    }

    public static ListF<Boolean> booleanList(boolean... elements) {
        return BooleanArray.arrayToList(elements);
    }

    public static ListF<Double> doubleList(double... elements) {
        return DoubleArray.arrayToList(elements);
    }

    public static ListF<Float> floatList(float... elements) {
        return FloatArray.arrayToList(elements);
    }

    public static <A> Vec2<A> vec(A a1, A a2) {
        return new Vec2<>(a1, a2);
    }

    public static <A> Vec3<A> vec(A a1, A a2, A a3) {
        return new Vec3<>(a1, a2, a3);
    }

    public static <A> Vec4<A> vec(A a1, A a2, A a3, A a4) {
        return new Vec4<>(a1, a2, a3, a4);
    }

    public static <A> Vec5<A> vec(A a1, A a2, A a3, A a4, A a5) {
        return new Vec5<>(a1, a2, a3, a4, a5);
    }

    public static <A> Vec6<A> vec(A a1, A a2, A a3, A a4, A a5, A a6) {
        return new Vec6<>(a1, a2, a3, a4, a5, a6);
    }

    public static <A> Vec7<A> vec(A a1, A a2, A a3, A a4, A a5, A a6, A a7) {
        return new Vec7<>(a1, a2, a3, a4, a5, a6, a7);
    }

    public static <A> Vec8<A> vec(A a1, A a2, A a3, A a4, A a5, A a6, A a7, A a8) {
        return new Vec8<>(a1, a2, a3, a4, a5, a6, a7, a8);
    }

    /**
     * Create extended mutable array list.
     */
    public static <T> ListF<T> arrayList() {
        return ArrayList.cons();
    }

    /** @deprecated inline */
    public static <A> ListF<A> arrayList(Collection<A> collection) {
        return toArrayList(collection);
    }

    /**
     * Create extended mutable array list containing given elements.
     */
    public static <A> ListF<A> toArrayList(Collection<A> collection) {
        return ArrayList.cons(collection);
    }

    /**
     * Create extended array list of elements.
     */
    @SuppressWarnings("unchecked")
    public static <A> ListF<A> arrayList(A... elements) {
        return ArrayList.cons(elements);
    }

    /** @deprecated inline */
    public static <A> ListF<A> arrayList(int initialCapacity) {
        return arrayListWithCapacity(initialCapacity);
    }

    /**
     * Create array list with given capacity.
     *
     * @see ArrayList#ArrayList(int)
     */
    public static <A> ListF<A> arrayListWithCapacity(int initialCapacity) {
        return ArrayList.cons(initialCapacity);
    }

    /** @deprecated inline */
    public static <A> Function0<ListF<A>> newArrayListF() {
        return Cf::arrayList;
    }

    /**
     * Singleton map.
     */
    public static <K, V> MapF<K, V> map(K key, V value) {
        return new SingletonMap<>(key, value);
    }

    /**
     * Map of either 1 or 2 entries.
     */
    public static <K, V> MapF<K, V> map(K key1, V value1, K key2, V value2) {
        return map(key1, value1).plus1(key2, value2);
    }

    /**
     * Map of 1..3 entries.
     */
    public static <K, V> MapF<K, V> map(K key1, V value1, K key2, V value2, K key3, V value3) {
        return map(key1, value1, key2, value2).plus1(key3, value3);
    }

    /**
     * Map of 4 entries.
     */
    public static <K, V> MapF<K, V> map(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4) {
        return map(key1, value1, key2, value2, key3, value3).plus1(key4, value4);
    }

    /** @deprecated inline */
    public static <K, V> MapF<K, V> map(Collection<Tuple2<K, V>> pairs) {
        return toMap(pairs);
    }

    /** Create map from sequence of entries */
    public static <K, V> MapF<K, V> toMap(Collection<Tuple2<K, V>> pairs) {
        return toHashMap(pairs);
    }

    /**
     * Immutable empty map.
     */
    @SuppressWarnings({"unchecked"})
    public static <K, V> MapF<K, V> map() {
        return EmptyMap.INSTANCE;
    }

    /**
     * Create extended linked list.
     */
    public static <T> ListF<T> linkedList() {
        return x(new LinkedList<>());
    }

    /**
     * Create hash map.
     *
     * @see HashMap
     */
    public static <K, V> MapF<K, V> hashMap() {
        return x(new HashMap<>());
    }

    /**
     * Create linked hash map.
     *
     * @see LinkedHashMap
     */
    public static <K, V> MapF<K, V> linkedHashMap() {
        return new DefaultMapF<K, V>(new LinkedHashMap<>()) {
            protected <A, B> MapF<A, B> newMutableMap() {
                return linkedHashMap();
            }
        };
    }

    /**
     * Create mutable linked hash set of specified elements.
     */
    @SuppressWarnings("unchecked")
    public static <T> SetF<T> linkedHashSet(T... elements) {
        return toLinkedHashSet(list(elements));
    }

    /**
     * Create mutable linked hash set of specified elements.
     */
    public static <T> SetF<T> toLinkedHashSet(Collection<T> collection) {
        return x(new LinkedHashSet<>(collection));
    }

    public static <K, V> MapF<K, V> hashMapWithExpectedSize(int expectedSize) {
        return x(new HashMap<>(initialCapacityForHashMapAndSet(expectedSize)));
    }

    /** @deprecated inline */
    public static <K, V> Function0<MapF<K, V>> newHashMapF() {
        return Cf::hashMap;
    }

    /**
     * Identity hash map.
     *
     * @see IdentityHashMap
     */
    @SuppressWarnings("serial")
    public static <K, V> MapF<K, V> identityHashMap() {
        return new DefaultMapF<K, V>(new IdentityHashMap<K, V>()) {
            protected <A, B> MapF<A, B> newMutableMap() {
                return identityHashMap();
            }
        };
    }

    /** @deprecated inline */
    public static <K, V> MapF<K, V> hashMap(Iterable<Tuple2<K, V>> entries) {
        return toHashMap(entries);
    }

    /**
     * Create hash map of specified entries.
     */
    public static <K, V> MapF<K, V> toHashMap(Iterable<Tuple2<K, V>> entries) {
        MapF<K, V> map = hashMap();
        map.putAll(entries);
        return map;
    }

    /** @deprecated inline */
    public static <K, V> MapF<K, V> hashMap(Map<K, V> entries) {
        return toHashMap(entries);
    }

    /**
     * Create hash map of specified entries.
     */
    public static <K, V> MapF<K, V> toHashMap(Map<K, V> entries) {
        MapF<K, V> map = hashMap();
        map.putAll(entries);
        return map;
    }

    /**
     * Wrapper around {@link ConcurrentHashMap}.
     */
    @SuppressWarnings("serial")
    public static <K, V> MapF<K, V> concurrentHashMap() {
        return new DefaultMapF<K, V>(new ConcurrentHashMap<K, V>()) {
            protected <A, B> MapF<A, B> newMutableMap() {
                return concurrentHashMap();
            }
        };
    }

    static int initialCapacityForHashMapAndSet(int expectedSize) {
        // see HashMap::putMapEntries
        return (int) ((((float) expectedSize) / 0.75f) + 1.0f);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static final ListIteratorF EMPTY_ITERATOR = new EmptyIterator();

    /**
     * Empty iterator.
     */
    @SuppressWarnings({"unchecked"})
    public static <T> IteratorF<T> emptyIterator() {
        return EMPTY_ITERATOR;
    }

    @SuppressWarnings({"unchecked"})
    public static <T> ListIteratorF<T> emptyListIterator() {
        return EMPTY_ITERATOR;
    }

    public static <T> IteratorF<T> singletonIterator(T t) {
        return new UnmodifiableSingletonIterator<>(t);
    }

    public static <T> ListIteratorF<T> singletonListIterator(T t) {
        return new UnmodifiableSingletonIterator<>(t);
    }

    /**
     * Immutable list of <code>element</code> repeated <code>times</code> times.
     */
    public static <T> ListF<T> repeat(final T element, final int times) {
        if (times == 0) return list();

        if (times < 0) throw new IllegalArgumentException();

        return new AbstractListF<T>() {
            public int size() {
                return times;
            }

            public T get(int index) {
                if (index < 0 || index >= size())
                    throw new IndexOutOfBoundsException();
                return element;
            }

            public SetF<T> unique() {
                return set(element);
            }

            @SuppressWarnings("deprecation")
            public boolean contains(Object o) {
                return unique().contains(o);
            }
        };
    }

    @SuppressWarnings({"unchecked"})
    public static <T> ListF<T> repeat(Function0<T> factory, int times) {
        Object[] elements = new Object[times];
        for (int i = 0; i < times; i++) {
            elements[i] = factory.apply();
        }
        return x((T[]) elements);
    }

    /**
     * Immutable list of integer in given range.
     *
     * @return empty list if <code>end &lt; start</code>
     */
    public static ListF<Integer> range(final int startInclusive, final int endExclusive) {
        if (startInclusive >= endExclusive) return list();
        return new Range(startInclusive, endExclusive);
    }

    /**
     * Immutable set of integer in given range.
     *
     * @return empty set if <code>end &lt; start</code>
     */
    private static SetF<Integer> rangeAsSet(final int startInclusive, final int endExclusive) {
        if (startInclusive >= endExclusive) return set();
        return new RangeAsSet(startInclusive, endExclusive);
    }

    private static class Range extends AbstractListF<Integer> implements Serializable {
        private static final long serialVersionUID = 5434453557597790996L;

        private final int startInclusive;
        private final int endExclusive;

        public Range(int startInclusive, int endExclusive) {
            this.startInclusive = startInclusive;
            this.endExclusive = endExclusive;
        }

        @Override
        public int size() {
            return endExclusive - startInclusive;
        }

        @Override
        public Integer get(int index) {
            if (index < 0 || index >= size())
                throw new IndexOutOfBoundsException();
            return startInclusive + index;
        }

        @Override
        public SetF<Integer> unique() {
            return rangeAsSet(startInclusive, endExclusive);
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean contains(Object o) {
            return unique().contains(o);
        }

        @Override
        public String toString() {
            return "[" + startInclusive + "," + endExclusive + ")";
        }
    }

    private static class RangeAsSet extends AbstractSetF<Integer> {
        private final int startInclusive;
        private final int endExclusive;

        public RangeAsSet(int startInclusive, int endExclusive) {
            this.startInclusive = startInclusive;
            this.endExclusive = endExclusive;
        }

        @Override
        public IteratorF<Integer> iterator() {
            return toList().iterator();
        }

        @Override
        public int size() {
            return endExclusive - startInclusive;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Integer)) return false;
            Integer v = (Integer) o;
            return v >= startInclusive && v < endExclusive;
        }

        @Override
        public ListF<Integer> toList() {
            return range(startInclusive, endExclusive);
        }

        @Override
        public String toString() {
            return toList().toString();
        }
    }

    /** Construct iterable */
    public static <T> IterableF<T> iterable(Supplier<Iterator<T>> iterator) {
        return new IterableF<T>() {
            @Override
            public IteratorF<T> iterator() {
                return x(iterator.get());
            }
        };
    }
   
    public static <T> ListF<T> flatten(Collection<? extends Collection<T>> l) {
        if (l.isEmpty()) return list();
        int flatSize = 0;
        for (Collection<T> t : l) {
            flatSize += t.size();
        }
        ArrayListF<T> result = new ArrayListF<>(flatSize);
        l.forEach(result::addAll);
        return result.convertToReadOnly();
    }

    @SafeVarargs
    public static <T> ListF<T> flattenArgs(Collection<T>... args) {
        return flatten(x(args));
    }
}

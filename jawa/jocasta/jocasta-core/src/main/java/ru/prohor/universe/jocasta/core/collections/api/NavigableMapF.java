package ru.prohor.universe.jocasta.core.collections.api;

import jakarta.annotation.Nonnull;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.function.Predicate;

public interface NavigableMapF<K, V> extends MapF<K, V>, NavigableMap<K, V> {
    @Nonnull
    @Override
    NavigableSetF<Map.Entry<K, V>> entrySet();

    @Nonnull
    @Override
    NavigableSetF<K> keySet();

    BidirectionalIteratorF<Map.Entry<K, V>> firstEntryIterator(Predicate<K> predicate);

    default BidirectionalIteratorF<Map.Entry<K, V>> lowerEntryIterator(K key) {
        Comparator<? super K> cmp = comparator();
        return firstEntryIterator(entryKey -> cmp.compare(entryKey, key) >= 0);
    }

    default BidirectionalIteratorF<K> floorKeyIterator(K key) {
        return lowerEntryIterator(key).map(Map.Entry::getKey);
    }

    default BidirectionalIteratorF<V> lowerValueIterator(K key) {
        return lowerEntryIterator(key).map(Map.Entry::getValue);
    }

    default BidirectionalIteratorF<Map.Entry<K, V>> higherEntryIterator(K key) {
        Comparator<? super K> cmp = comparator();
        return firstEntryIterator(entryKey -> cmp.compare(entryKey, key) > 0);
    }

    default BidirectionalIteratorF<K> higherKeyIterator(K key) {
        return lowerEntryIterator(key).map(Map.Entry::getKey);
    }

    default BidirectionalIteratorF<V> higherValueIterator(K key) {
        return lowerEntryIterator(key).map(Map.Entry::getValue);
    }
}

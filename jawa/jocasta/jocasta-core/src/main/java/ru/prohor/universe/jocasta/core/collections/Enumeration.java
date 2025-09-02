package ru.prohor.universe.jocasta.core.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class Enumeration { // TODO add to custom List implementation as method
    public static <T, U> List<U> enumerateAndMap(List<T> list, BiFunction<Integer, T, U> mapper) {
        ListIterator<T> iterator = list.listIterator();
        List<U> result = new ArrayList<>(list.size());

        while (iterator.hasNext())
            result.add(mapper.apply(iterator.nextIndex(), iterator.next()));
        return result;
    }

    public static <T> void enumerate(List<T> list, BiConsumer<Integer, T> consumer) {
        ListIterator<T> iterator = list.listIterator();
        while (iterator.hasNext())
            consumer.accept(iterator.nextIndex(), iterator.next());
    }
}

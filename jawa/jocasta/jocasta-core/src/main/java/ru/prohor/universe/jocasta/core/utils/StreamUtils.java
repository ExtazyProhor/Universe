package ru.prohor.universe.jocasta.core.utils;

import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamUtils {
    public static <T> Collector<T, ?, Stream<T>> toShuffledStream() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    Collections.shuffle(list);
                    return list.stream();
                }
        );
    }
}

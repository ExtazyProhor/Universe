package ru.prohor.universe.jocasta.morphia.query;

import dev.morphia.query.Sort;
import ru.prohor.universe.jocasta.core.features.fieldref.FieldReference;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public final class MongoSorts {
    private MongoSorts() {}

    @SafeVarargs
    public static <T, C extends Comparable<C>> MongoSort<T> ascending(FieldReference<T, C>... fields) {
        return ascending(Arrays.asList(fields));
    }

    public static <T, C extends Comparable<C>> MongoSort<T> ascending(List<FieldReference<T, C>> fields) {
        if (fields.isEmpty())
            throw new IllegalArgumentException("fields list can not be empty");
        return new MongoSort<>() {
            @Override
            public Comparator<T> inMemory() {
                return createComparator(fields);
            }

            @Override
            public Sort[] morphia() {
                return fields.stream().map(FieldReference::name).map(Sort::ascending).toArray(Sort[]::new);
            }
        };
    }

    @SafeVarargs
    public static <T, C extends Comparable<C>> MongoSort<T> descending(FieldReference<T, C>... fields) {
        return descending(Arrays.asList(fields));
    }

    public static <T, C extends Comparable<C>> MongoSort<T> descending(List<FieldReference<T, C>> fields) {
        if (fields.isEmpty())
            throw new IllegalArgumentException("fields list can not be empty");
        return new MongoSort<>() {
            @Override
            public Comparator<T> inMemory() {
                return createComparator(fields).reversed();
            }

            @Override
            public Sort[] morphia() {
                return fields.stream().map(FieldReference::name).map(Sort::descending).toArray(Sort[]::new);
            }
        };
    }

    private static <T, C extends Comparable<C>> Comparator<T> createComparator(List<FieldReference<T, C>> fields) {
        Comparator<T> comparator = Comparator.comparing(t -> fields.getFirst().get(t));
        boolean first = true;
        for (FieldReference<T, C> fieldReference : fields) {
            if (first) {
                first = false;
                continue;
            }
            comparator = comparator.thenComparing(fieldReference::get);
        }
        return comparator;
    }

    @SafeVarargs
    public static <T> MongoSort<T> compound(MongoSort<T>... sorts) {
        return compound(Arrays.asList(sorts));
    }

    public static <T> MongoSort<T> compound(List<MongoSort<T>> sorts) {
        if (sorts.isEmpty())
            throw new IllegalArgumentException("sorts list can not be empty");
        return new MongoSort<>() {
            @Override
            public Comparator<T> inMemory() {
                Comparator<T> comparator = sorts.getFirst().inMemory();
                boolean first = true;
                for (MongoSort<T> sort : sorts) {
                    if (first) {
                        first = false;
                        continue;
                    }
                    comparator = comparator.thenComparing(sort.inMemory());
                }
                return comparator;
            }

            @Override
            public Sort[] morphia() {
                return sorts.stream().flatMap(sort -> Arrays.stream(sort.morphia())).toArray(Sort[]::new);
            }
        };
    }
}

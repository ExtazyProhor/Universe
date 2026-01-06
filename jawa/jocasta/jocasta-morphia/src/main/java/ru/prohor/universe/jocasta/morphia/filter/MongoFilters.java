package ru.prohor.universe.jocasta.morphia.filter;

import dev.morphia.query.filters.Filter;
import dev.morphia.query.filters.Filters;
import ru.prohor.universe.jocasta.core.features.fieldref.FieldProperties;
import ru.prohor.universe.jocasta.core.functional.MonoPredicate;

import java.util.Arrays;
import java.util.List;

public final class MongoFilters {
    private MongoFilters() {}

    public static <T, R> MongoFilter<T> eq(FieldProperties<T, R> fieldProperties, R value) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> fieldProperties.getO(t).map(it -> it.equals(value)).orElse(false);
            }

            @Override
            public Filter morphia() {
                return Filters.eq(fieldProperties.name(), value);
            }
        };
    }

    public static <T, R> MongoFilter<T> ne(FieldProperties<T, R> fieldProperties, R value) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> fieldProperties.getO(t).map(it -> !it.equals(value)).orElse(false);
            }

            @Override
            public Filter morphia() {
                return Filters.ne(fieldProperties.name(), value);
            }
        };
    }

    public static <T> MongoFilter<T> exists(FieldProperties<T, ?> fieldProperties) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> fieldProperties.getO(t).isPresent();
            }

            @Override
            public Filter morphia() {
                return Filters.exists(fieldProperties.name());
            }
        };
    }

    @SafeVarargs
    public static <T> MongoFilter<T> and(MongoFilter<T>... filters) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> Arrays.stream(filters).allMatch(filter -> filter.inMemory().test(t));
            }

            @Override
            public Filter morphia() {
                return Filters.and(Arrays.stream(filters).map(MongoFilter::morphia).toArray(Filter[]::new));
            }
        };
    }

    public static <T> MongoFilter<T> and(List<MongoFilter<T>> filters) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> filters.stream().allMatch(filter -> filter.inMemory().test(t));
            }

            @Override
            public Filter morphia() {
                return Filters.and(filters.stream().map(MongoFilter::morphia).toArray(Filter[]::new));
            }
        };
    }

    @SafeVarargs
    public static <T> MongoFilter<T> or(MongoFilter<T>... filters) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> Arrays.stream(filters).anyMatch(filter -> filter.inMemory().test(t));
            }

            @Override
            public Filter morphia() {
                return Filters.or(Arrays.stream(filters).map(MongoFilter::morphia).toArray(Filter[]::new));
            }
        };
    }

    public static <T> MongoFilter<T> or(List<MongoFilter<T>> filters) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> filters.stream().anyMatch(filter -> filter.inMemory().test(t));
            }

            @Override
            public Filter morphia() {
                return Filters.or(filters.stream().map(MongoFilter::morphia).toArray(Filter[]::new));
            }
        };
    }

    public static <T, R> MongoFilter<T> in(FieldProperties<T, R> fieldProperties, List<R> list) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> fieldProperties.getO(t).map(list::contains).orElse(false);
            }

            @Override
            public Filter morphia() {
                return Filters.in(fieldProperties.name(), list);
            }
        };
    }

    public static <T, R> MongoFilter<T> nin(FieldProperties<T, R> fieldProperties, List<R> list) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> !fieldProperties.getO(t).map(list::contains).orElse(false);
            }

            @Override
            public Filter morphia() {
                return Filters.nin(fieldProperties.name(), list);
            }
        };
    }

    public static <T> MongoFilter<T> gt(FieldProperties<T, Integer> fieldProperties, Integer value) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> fieldProperties.getO(t).map(it -> it > value).orElse(false);
            }

            @Override
            public Filter morphia() {
                return Filters.gt(fieldProperties.name(), value);
            }
        };
    }

    public static <T> MongoFilter<T> gt(FieldProperties<T, Long> fieldProperties, Long value) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> fieldProperties.getO(t).map(it -> it > value).orElse(false);
            }

            @Override
            public Filter morphia() {
                return Filters.gt(fieldProperties.name(), value);
            }
        };
    }

    public static <T> MongoFilter<T> gt(FieldProperties<T, Float> fieldProperties, Float value) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> fieldProperties.getO(t).map(it -> it > value).orElse(false);
            }

            @Override
            public Filter morphia() {
                return Filters.gt(fieldProperties.name(), value);
            }
        };
    }

    public static <T> MongoFilter<T> gt(FieldProperties<T, Double> fieldProperties, Double value) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> fieldProperties.getO(t).map(it -> it > value).orElse(false);
            }

            @Override
            public Filter morphia() {
                return Filters.gt(fieldProperties.name(), value);
            }
        };
    }

    public static <T> MongoFilter<T> gte(FieldProperties<T, Integer> fieldProperties, Integer value) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> fieldProperties.getO(t).map(it -> it >= value).orElse(false);
            }

            @Override
            public Filter morphia() {
                return Filters.gte(fieldProperties.name(), value);
            }
        };
    }

    public static <T> MongoFilter<T> gte(FieldProperties<T, Long> fieldProperties, Long value) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> fieldProperties.getO(t).map(it -> it >= value).orElse(false);
            }

            @Override
            public Filter morphia() {
                return Filters.gte(fieldProperties.name(), value);
            }
        };
    }

    public static <T> MongoFilter<T> gte(FieldProperties<T, Float> fieldProperties, Float value) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> fieldProperties.getO(t).map(it -> it >= value).orElse(false);
            }

            @Override
            public Filter morphia() {
                return Filters.gte(fieldProperties.name(), value);
            }
        };
    }

    public static <T> MongoFilter<T> gte(FieldProperties<T, Double> fieldProperties, Double value) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> fieldProperties.getO(t).map(it -> it >= value).orElse(false);
            }

            @Override
            public Filter morphia() {
                return Filters.gte(fieldProperties.name(), value);
            }
        };
    }

    public static <T> MongoFilter<T> lt(FieldProperties<T, Integer> fieldProperties, Integer value) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> fieldProperties.getO(t).map(it -> it < value).orElse(false);
            }

            @Override
            public Filter morphia() {
                return Filters.lt(fieldProperties.name(), value);
            }
        };
    }

    public static <T> MongoFilter<T> lt(FieldProperties<T, Long> fieldProperties, Long value) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> fieldProperties.getO(t).map(it -> it < value).orElse(false);
            }

            @Override
            public Filter morphia() {
                return Filters.lt(fieldProperties.name(), value);
            }
        };
    }

    public static <T> MongoFilter<T> lt(FieldProperties<T, Float> fieldProperties, Float value) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> fieldProperties.getO(t).map(it -> it < value).orElse(false);
            }

            @Override
            public Filter morphia() {
                return Filters.lt(fieldProperties.name(), value);
            }
        };
    }

    public static <T> MongoFilter<T> lt(FieldProperties<T, Double> fieldProperties, Double value) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> fieldProperties.getO(t).map(it -> it < value).orElse(false);
            }

            @Override
            public Filter morphia() {
                return Filters.lt(fieldProperties.name(), value);
            }
        };
    }

    public static <T> MongoFilter<T> lte(FieldProperties<T, Integer> fieldProperties, Integer value) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> fieldProperties.getO(t).map(it -> it <= value).orElse(false);
            }

            @Override
            public Filter morphia() {
                return Filters.lte(fieldProperties.name(), value);
            }
        };
    }

    public static <T> MongoFilter<T> lte(FieldProperties<T, Long> fieldProperties, Long value) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> fieldProperties.getO(t).map(it -> it <= value).orElse(false);
            }

            @Override
            public Filter morphia() {
                return Filters.lte(fieldProperties.name(), value);
            }
        };
    }

    public static <T> MongoFilter<T> lte(FieldProperties<T, Float> fieldProperties, Float value) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> fieldProperties.getO(t).map(it -> it <= value).orElse(false);
            }

            @Override
            public Filter morphia() {
                return Filters.lte(fieldProperties.name(), value);
            }
        };
    }

    public static <T> MongoFilter<T> lte(FieldProperties<T, Double> fieldProperties, Double value) {
        return new MongoFilter<>() {
            @Override
            public MonoPredicate<T> inMemory() {
                return t -> fieldProperties.getO(t).map(it -> it <= value).orElse(false);
            }

            @Override
            public Filter morphia() {
                return Filters.lte(fieldProperties.name(), value);
            }
        };
    }
}

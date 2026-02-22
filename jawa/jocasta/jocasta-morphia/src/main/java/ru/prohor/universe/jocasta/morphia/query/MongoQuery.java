package ru.prohor.universe.jocasta.morphia.query;

import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilter;

public class MongoQuery<T> {
    private Opt<MongoFilter<T>> filter = Opt.empty();
    private Opt<MongoSort<T>> sort = Opt.empty();
    private Opt<Integer> skip = Opt.empty();
    private Opt<Integer> limit = Opt.empty();

    public MongoQuery<T> filter(MongoFilter<T> filter) {
        this.filter = Opt.of(filter);
        return this;
    }

    public MongoQuery<T> sort(MongoSort<T> sort) {
        this.sort = Opt.of(sort);
        return this;
    }

    public MongoQuery<T> skip(int skip) {
        this.skip = Opt.of(skip);
        return this;
    }

    public MongoQuery<T> limit(int limit) {
        this.limit = Opt.of(limit);
        return this;
    }

    public Opt<MongoFilter<T>> getFilter() {
        return filter;
    }

    public Opt<MongoSort<T>> getSort() {
        return sort;
    }

    public Opt<Integer> getSkip() {
        return skip;
    }

    public Opt<Integer> getLimit() {
        return limit;
    }
}

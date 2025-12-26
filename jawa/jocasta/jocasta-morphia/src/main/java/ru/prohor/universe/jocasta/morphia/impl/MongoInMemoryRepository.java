package ru.prohor.universe.jocasta.morphia.impl;

import dev.morphia.query.filters.Filter;
import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.collections.PaginationResult;
import ru.prohor.universe.jocasta.core.collections.Paginator;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.MonoPredicate;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.MongoTextSearchResult;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class MongoInMemoryRepository<T extends MongoEntityPojo<?>> implements MongoRepository<T> {
    private static final UnsupportedOperationException UNSUPPORTED_TEXT_SEARCH = new UnsupportedOperationException(
            "Cannot use text search without textSearchPredicate"
    );

    private final Map<ObjectId, T> collection;
    private final Function<T, ObjectId> idExtractor;
    private final Opt<BiPredicate<T, String>> textSearchPredicate;
    private final Class<T> type;

    public MongoInMemoryRepository(Function<T, ObjectId> idExtractor, Class<T> type) {
        this(idExtractor, null, type);
    }

    public MongoInMemoryRepository(
            Function<T, ObjectId> idExtractor,
            BiPredicate<T, String> textSearchPredicate,
            Class<T> type
    ) {
        this.collection = Collections.synchronizedMap(new HashMap<>());
        this.idExtractor = idExtractor;
        this.textSearchPredicate = Opt.ofNullable(textSearchPredicate);
        this.type = type;
    }

    @Override
    public List<T> findAll() {
        return collection.values().stream().toList();
    }

    @Override
    public long countDocuments() {
        return collection.size();
    }

    @Override
    public Opt<T> findById(ObjectId id) {
        return Opt.ofNullable(collection.get(id));
    }

    @Override
    public List<T> findAllByIds(List<ObjectId> ids) {
        return ids.stream()
                .map(this::findById)
                .filter(Opt::isPresent)
                .map(Opt::get)
                .toList();
    }

    @Override
    public List<T> find(MongoFilter<T> filter) {
        return collection.values().stream().filter(element -> filter.inMemory().test(element)).toList();
    }

    @Override
    public List<T> find(Filter filter, MonoPredicate<T> manualFilter) {
        return collection.values()
                .stream()
                .filter(manualFilter)
                .toList();
    }

    @Override
    public void save(T entity) {
        collection.put(idExtractor.apply(entity), entity);
    }

    @Override
    public void save(List<T> entities) {
        entities.forEach(this::save);
    }

    @Override
    public Opt<T> deleteById(ObjectId id) {
        return Opt.ofNullable(collection.remove(id));
    }

    @Override
    public List<T> findByText(String text) {
        if (textSearchPredicate.isEmpty())
            throw UNSUPPORTED_TEXT_SEARCH;
        return collection.values()
                .stream()
                .filter(t -> textSearchPredicate.get().test(t, text))
                .toList();
    }

    @Override
    public MongoTextSearchResult<T> findByText(String text, int page, int pageSize) {
        if (textSearchPredicate.isEmpty())
            throw UNSUPPORTED_TEXT_SEARCH;
        List<T> all = findByText(text);
        PaginationResult<T> paginationResult = Paginator.richPaginateOrLastPage(all, page, pageSize);
        return new MongoTextSearchResult<>(
                paginationResult.values(),
                all.size(),
                paginationResult.page(),
                paginationResult.lastPage()
        );
    }

    @Override
    public Class<T> type() {
        return type;
    }
}

package ru.prohor.universe.yahtzee.data;

import dev.morphia.query.filters.Filter;
import dev.morphia.query.updates.UpdateOperator;
import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.collections.Opt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class MongoInMemoryRepository<T> implements MongoRepository<T> {
    private static final UnsupportedOperationException UNSUPPORTED_FILTER = new UnsupportedOperationException(
            "Cannot use Mongo filter in memory"
    );
    private static final UnsupportedOperationException UNSUPPORTED_TEXT_SEARCH = new UnsupportedOperationException(
            "Cannot use text search without textSearchPredicate"
    );

    private final Map<ObjectId, T> collection;
    private final Function<T, ObjectId> idExtractor;
    private final Opt<BiPredicate<T, String>> textSearchPredicate;

    public MongoInMemoryRepository(Function<T, ObjectId> idExtractor) {
        this.collection = new HashMap<>();
        this.idExtractor = idExtractor;
        this.textSearchPredicate = Opt.empty();
    }

    public MongoInMemoryRepository(
            Function<T, ObjectId> idExtractor,
            BiPredicate<T, String> textSearchPredicate
    ) {
        this.collection = new HashMap<>();
        this.idExtractor = idExtractor;
        this.textSearchPredicate = Opt.of(textSearchPredicate);
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
    public List<T> find(Filter filter) {
        throw UNSUPPORTED_FILTER;
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
    public void update(Filter filter, UpdateOperator updateOperator) {
        throw UNSUPPORTED_FILTER;
    }

    @Override
    public void deleteById(ObjectId id) {
        collection.remove(id);
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
        return new MongoTextSearchResult<>(
                all.stream().skip((long) page * pageSize).limit(pageSize).toList(),
                all.size()
        );
    }
}

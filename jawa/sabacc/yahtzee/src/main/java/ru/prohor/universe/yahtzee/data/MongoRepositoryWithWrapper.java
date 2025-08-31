package ru.prohor.universe.yahtzee.data;

import dev.morphia.Datastore;
import dev.morphia.query.filters.Filter;
import dev.morphia.query.updates.UpdateOperator;
import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.collections.Opt;

import java.util.List;
import java.util.function.Function;

public final class MongoRepositoryWithWrapper<T, W extends MongoEntityPojo<T>> {
    private final BaseMongoRepository<T> base;
    private final Function<T, W> wrapFunction;

    public MongoRepositoryWithWrapper(
            Function<T, W> wrapFunction,
            Datastore datastore,
            Class<T> type,
            String collection
    ) {
        this.base = new BaseMongoRepository<>(datastore, type, collection);
        this.wrapFunction = wrapFunction;
    }

    public Opt<W> findById(ObjectId id) {
        return base.findById(id).map(wrapFunction);
    }

    public List<W> find(Filter filter) {
        return base.find(filter).stream().map(wrapFunction).toList();
    }

    public void save(W entity) {
        base.save(entity.toDto());
    }

    public void save(List<W> entities) {
        base.save(entities.stream().map(MongoEntityPojo::toDto).toList());
    }

    public void update(Filter filter, UpdateOperator updateOperator) {
        base.update(filter, updateOperator);
    }

    public void deleteById(ObjectId id) {
        base.deleteById(id);
    }

    public List<W> findByText(String text) {
        return base.findByText(text).stream().map(wrapFunction).toList();
    }

    public MongoTextSearchResult<W> findByText(String text, int page, int pageSize) {
        MongoTextSearchResult<T> result = base.findByText(text, page, pageSize);
        return new MongoTextSearchResult<>(
                result.entities().stream().map(wrapFunction).toList(),
                result.total()
        );
    }
}

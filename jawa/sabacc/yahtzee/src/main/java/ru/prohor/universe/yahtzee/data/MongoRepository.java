package ru.prohor.universe.yahtzee.data;

import dev.morphia.Datastore;
import dev.morphia.query.filters.Filter;
import dev.morphia.query.updates.UpdateOperator;
import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.collections.Opt;

import java.util.List;

public final class MongoRepository<T> {
    private final BaseMongoRepository<T> base;

    public MongoRepository(Datastore datastore, Class<T> type, String collection) {
        this.base = new BaseMongoRepository<>(datastore, type, collection);
    }

    public Opt<T> findById(ObjectId id) {
        return base.findById(id);
    }

    public List<T> find(Filter filter) {
        return base.find(filter);
    }

    public void save(T entity) {
        base.save(entity);
    }

    public void save(List<T> entities) {
        base.save(entities);
    }

    public void update(Filter filter, UpdateOperator updateOperator) {
        base.update(filter, updateOperator);
    }

    public void deleteById(ObjectId id) {
        base.deleteById(id);
    }

    public List<T> findByText(String text) {
        return base.findByText(text);
    }

    public MongoTextSearchResult<T> findByText(String text, int page, int pageSize) {
        return base.findByText(text, page, pageSize);
    }
}

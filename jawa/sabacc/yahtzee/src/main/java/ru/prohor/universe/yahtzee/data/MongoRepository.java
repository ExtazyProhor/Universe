package ru.prohor.universe.yahtzee.data;

import dev.morphia.query.filters.Filter;
import dev.morphia.query.updates.UpdateOperator;
import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.util.List;

public interface MongoRepository<T> {
    Opt<T> findById(ObjectId id);

    List<T> findAllByIds(List<ObjectId> ids);

    List<T> find(Filter filter);

    void save(T entity);

    void save(List<T> entities);

    void update(Filter filter, UpdateOperator updateOperator);

    void deleteById(ObjectId id);

    List<T> findByText(String text);

    MongoTextSearchResult<T> findByText(String text, int page, int pageSize);
}

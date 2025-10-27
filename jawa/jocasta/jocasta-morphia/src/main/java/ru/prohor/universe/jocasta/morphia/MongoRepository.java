package ru.prohor.universe.jocasta.morphia;

import dev.morphia.query.filters.Filter;
import dev.morphia.query.updates.UpdateOperator;
import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.util.HashSet;
import java.util.List;

public interface MongoRepository<T> {
    List<T> findAll();

    long countDocuments();

    Opt<T> findById(ObjectId id);

    default T ensuredFindById(ObjectId id) {
        return findById(id).orElseThrow(
                () -> new MongoDatabaseException("ensured method was called with absent value")
        );
    }

    List<T> findAllByIds(List<ObjectId> ids);

    default List<T> ensuredFindAllByIds(List<ObjectId> ids) {
        if (new HashSet<>(ids).size() != ids.size())
            throw new MongoDatabaseException("list of ids has duplicates: " + ids);
        List<T> entities = findAllByIds(ids);
        if (entities.size() != ids.size())
            throw new MongoDatabaseException("ensured method was called with absent value or values");
        return entities;
    }

    List<T> find(Filter filter);

    void save(T entity);

    void save(List<T> entities);

    void update(Filter filter, UpdateOperator updateOperator);

    void deleteById(ObjectId id);

    List<T> findByText(String text);

    MongoTextSearchResult<T> findByText(String text, int page, int pageSize);
}

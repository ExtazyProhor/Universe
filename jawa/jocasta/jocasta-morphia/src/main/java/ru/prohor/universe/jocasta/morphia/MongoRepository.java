package ru.prohor.universe.jocasta.morphia;

import dev.morphia.query.filters.Filter;
import dev.morphia.query.updates.UpdateOperator;
import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    default Map<ObjectId, T> findAllByIdsAsMap(List<ObjectId> ids, MonoFunction<T, ObjectId> keyExtractor) {
        return findAllByIds(ids).stream().collect(Collectors.toMap(
                keyExtractor,
                MonoFunction.identity()
        ));
    }

    default List<T> ensuredFindAllByIds(List<ObjectId> ids) {
        if (new HashSet<>(ids).size() != ids.size())
            throw new MongoDatabaseException("list of ids has duplicates: " + ids);
        List<T> entities = findAllByIds(ids);
        if (entities.size() != ids.size())
            throw new MongoDatabaseException("ensured method was called with absent value or values");
        return entities;
    }

    default Map<ObjectId, T> ensuredFindAllByIdsAsMap(List<ObjectId> ids, MonoFunction<T, ObjectId> keyExtractor) {
        return ensuredFindAllByIds(ids).stream().collect(Collectors.toMap(
                keyExtractor,
                MonoFunction.identity()
        ));
    }

    List<T> find(Filter filter);

    void save(T entity);

    void save(List<T> entities);

    void update(Filter filter, UpdateOperator updateOperator);

    void deleteById(ObjectId id);

    List<T> findByText(String text);

    MongoTextSearchResult<T> findByText(String text, int page, int pageSize);
}

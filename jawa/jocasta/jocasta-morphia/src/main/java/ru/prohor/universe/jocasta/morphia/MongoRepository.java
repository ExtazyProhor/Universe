package ru.prohor.universe.jocasta.morphia;

import dev.morphia.query.filters.Filter;
import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.MonoConsumer;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.core.functional.MonoPredicate;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilter;
import ru.prohor.universe.jocasta.morphia.query.MongoQuery;

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

    List<T> find(MongoFilter<T> filter);

    List<T> find(MongoQuery<T> query);

    @Deprecated
    List<T> find(Filter filter, MonoPredicate<T> manualFilter);

    void save(T entity);

    void save(List<T> entities);

    Opt<T> deleteById(ObjectId id);

    default T safeUpdate(ObjectId id, MonoFunction<T, T> updateFunction) {
        return this.withTransaction(tx -> {
            T updated = updateFunction.apply(tx.ensuredFindById(id));
            tx.save(updated);
            return updated;
        });
    }

    default Opt<T> safeUpdateO(ObjectId id, MonoFunction<Opt<T>, Opt<T>> updateFunction) {
        return this.withTransaction(tx -> {
            Opt<T> updated = updateFunction.apply(tx.findById(id));
            updated.ifPresent(tx::save);
            return updated;
        });
    }

    List<T> findByText(String text);

    MongoTextSearchResult<T> findByText(String text, int page, int pageSize);

    Class<T> type();

    <E> E withTransaction(MonoFunction<MongoRepository<T>, E> transaction);

    void withTransaction(MonoConsumer<MongoRepository<T>> transaction);
}

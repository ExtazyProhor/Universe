package ru.prohor.universe.jocasta.morphia;

import dev.morphia.Datastore;
import dev.morphia.query.filters.Filter;
import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.MonoPredicate;

import java.util.List;
import java.util.function.Function;

public class MongoMorphiaRepository<T> implements MongoRepository<T> {
    final AbstractMongoMorphiaRepository<?, T> repository;

    MongoMorphiaRepository(AbstractMongoMorphiaRepository<?, T> repository) {
        this.repository = repository;
    }

    public static <E, W extends MongoEntityPojo<E>> MongoMorphiaRepository<W> createRepository(
            Datastore datastore,
            Class<E> type,
            Function<E, W> wrapFunction
    ) {
        return new MongoMorphiaRepository<>(new AbstractMongoMorphiaRepository<>(
                datastore,
                type,
                wrapFunction,
                W::toDto
        ));
    }

    // TODO tests
    public static <T> MongoMorphiaRepository<T> createRepository(
            Datastore datastore,
            Class<T> type
    ) {
        Function<T, T> noWrap = t -> t;
        return new MongoMorphiaRepository<>(new AbstractMongoMorphiaRepository<>(datastore, type, noWrap, noWrap));
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public long countDocuments() {
        return repository.countDocuments();
    }

    @Override
    public Opt<T> findById(ObjectId id) {
        return repository.findById(id);
    }

    @Override
    public List<T> findAllByIds(List<ObjectId> ids) {
        return repository.findAllByIds(ids);
    }

    @Override
    public List<T> find(Filter filter) {
        return repository.find(filter);
    }

    @Override
    public List<T> find(Filter filter, MonoPredicate<T> manualFilter) {
        return repository.find(filter);
    }

    @Override
    public void save(T entity) {
        repository.save(entity);
    }

    @Override
    public void save(List<T> entities) {
        repository.save(entities);
    }

    @Override
    public void deleteById(ObjectId id) {
        repository.deleteById(id);
    }

    @Override
    public List<T> findByText(String text) {
        return repository.findByText(text);
    }

    @Override
    public MongoTextSearchResult<T> findByText(String text, int page, int pageSize) {
        return repository.findByText(text, page, pageSize);
    }
}

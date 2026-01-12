package ru.prohor.universe.jocasta.morphia.impl;

import com.mongodb.client.MongoCollection;
import dev.morphia.Datastore;
import dev.morphia.transactions.MorphiaSession;
import org.bson.Document;
import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.MonoConsumer;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.MongoTextSearchResult;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilter;
import ru.prohor.universe.jocasta.morphia.query.MongoQuery;

import java.util.List;
import java.util.function.Function;

public class MongoMorphiaRepository<T> implements MongoRepository<T> {
    private final Class<T> type;
    final AbstractMongoMorphiaRepository<?, T> repository;

    private MongoMorphiaRepository(AbstractMongoMorphiaRepository<?, T> repository, Class<T> type) {
        this.repository = repository;
        this.type = type;
    }

    public static <E, W extends MongoEntityPojo<E>> MongoMorphiaRepository<W> createRepository(
            Datastore datastore,
            Class<W> wrapperType,
            Class<E> dtoType,
            Function<E, W> wrapFunction
    ) {
        return new MongoMorphiaRepository<>(
                new AbstractMongoMorphiaRepository<>(datastore, dtoType, wrapFunction, W::toDto),
                wrapperType
        );
    }

    // TODO tests
    public static <T> MongoMorphiaRepository<T> createRepository(
            Datastore datastore,
            Class<T> type
    ) {
        return new MongoMorphiaRepository<>(
                new AbstractMongoMorphiaRepository<>(datastore, type, Function.identity(), Function.identity()),
                type
        );
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
    public List<T> find(MongoFilter<T> filter) {
        return repository.find(filter.morphia());
    }

    @Override
    public List<T> find(MongoQuery<T> query) {
        return repository.find(query);
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
    public Opt<T> deleteById(ObjectId id) {
        return repository.deleteById(id);
    }

    @Override
    public List<T> findByText(String text) {
        return repository.findByText(text);
    }

    @Override
    public MongoTextSearchResult<T> findByText(String text, int page, int pageSize) {
        return repository.findByText(text, page, pageSize);
    }

    @Override
    public Class<T> type() {
        return type;
    }

    public MongoCollection<Document> getCollection() {
        return repository.getCollection();
    }

    @Override
    public <E> E withTransaction(MonoFunction<MongoRepository<T>, E> transaction) {
        return repository.withTransaction(session -> {
            return transaction.apply(wrapWithTransaction(session));
        });
    }

    @Override
    public void withTransaction(MonoConsumer<MongoRepository<T>> transaction) {
        repository.withTransaction(session -> {
            transaction.accept(wrapWithTransaction(session));
        });
    }

    MongoRepository<T> wrapWithTransaction(MorphiaSession session) {
        return new MongoMorphiaRepository<>(repository.copy(session), type());
    }
}

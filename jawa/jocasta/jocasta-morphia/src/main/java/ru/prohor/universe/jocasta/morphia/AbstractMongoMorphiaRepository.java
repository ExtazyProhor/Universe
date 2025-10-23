package ru.prohor.universe.jocasta.morphia;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import dev.morphia.Datastore;
import dev.morphia.annotations.Entity;
import dev.morphia.query.filters.Filter;
import dev.morphia.query.updates.UpdateOperator;
import org.bson.Document;
import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class AbstractMongoMorphiaRepository<T, W> {
    private static final String NO_ENTITY = "entity does not exists, but was found";
    private static final String SCORE = "score";
    private static final String ID = "_id";

    private static final UnaryOperator<FindIterable<Document>> NO_PAGING = UnaryOperator.identity();

    private final MongoCollection<Document> collection;
    private final Datastore datastore;
    private final Class<T> type;

    private final Function<T, W> wrapFunction;
    private final Function<W, T> unwrapFunction;

    AbstractMongoMorphiaRepository(
            Datastore datastore,
            Class<T> type,
            Function<T, W> wrapFunction,
            Function<W, T> unwrapFunction
    ) {
        this.type = type;
        this.datastore = datastore;
        this.collection = datastore.getDatabase().getCollection(getCollectionName());
        this.wrapFunction = wrapFunction;
        this.unwrapFunction = unwrapFunction;
    }

    AbstractMongoMorphiaRepository<T, W> copy(Datastore datastore) {
        return new AbstractMongoMorphiaRepository<>(datastore, type, wrapFunction, unwrapFunction);
    }

    List<W> findAll() {
        return datastore.find(type).stream().map(wrapFunction).toList();
    }

    long countDocuments() {
        return collection.countDocuments();
    }

    Opt<W> findById(ObjectId id) {
        // TODO убрать dev.morphia.query.filters. путем вынесения фильтров в отдельный класс
        // TODO + ссылки на поля классов для фильтрации
        return Opt.ofNullable(
                datastore.find(type).filter(dev.morphia.query.filters.Filters.eq(ID, id)).first()
        ).map(wrapFunction);
    }

    List<W> findAllByIds(List<ObjectId> ids) {
        return datastore.find(type)
                .filter(dev.morphia.query.filters.Filters.in(ID, ids))
                .stream()
                .map(wrapFunction)
                .toList();
    }

    List<W> find(Filter filter) {
        return datastore.find(type).filter(filter).stream().map(wrapFunction).toList();
    }

    void save(W entity) {
        datastore.save(unwrapFunction.apply(entity));
    }

    void save(List<W> entities) {
        datastore.save(entities.stream().map(unwrapFunction).toList());
    }

    @SuppressWarnings("all")
    void update(Filter filter, UpdateOperator updateOperator) {
        datastore.find(type)
                .filter(filter)
                .update(updateOperator).execute();
    }

    void deleteById(ObjectId id) {
        collection.deleteOne(Filters.eq(ID, id));
    }

    List<W> findByText(String text) {
        return findEntities(text, NO_PAGING);
    }

    MongoTextSearchResult<W> findByText(String text, int page, int pageSize) {
        return new MongoTextSearchResult<>(
                findEntities(text, found -> found.skip(page * pageSize).limit(pageSize)),
                collection.countDocuments(Filters.text(text))
        );
    }

    private String getCollectionName() {
        Entity annotation = type.getAnnotation(Entity.class);
        if (annotation == null)
            throw new IllegalArgumentException(
                    "Entity class " + type.getSimpleName() + " must have @Entity annotation"
            );
        return annotation.value();
    }

    private List<W> findEntities(String text, UnaryOperator<FindIterable<Document>> paging) {
        FindIterable<Document> findIterable = collection.find(Filters.text(text))
                .projection(Projections.fields(Projections.include(ID), Projections.metaTextScore(SCORE)))
                .sort(Sorts.orderBy(Sorts.metaTextScore(SCORE), Sorts.ascending(ID)));
        findIterable = paging.apply(findIterable);

        return findIterable.map(
                document -> findById(document.getObjectId(ID)).orElseThrow(
                        () -> new MongoDatabaseException(NO_ENTITY)
                )
        ).into(new ArrayList<>());
    }
}

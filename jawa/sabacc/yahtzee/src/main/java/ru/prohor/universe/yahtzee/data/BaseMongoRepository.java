package ru.prohor.universe.yahtzee.data;

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
import ru.prohor.universe.jocasta.core.collections.Opt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class BaseMongoRepository<T> {
    private static final String NO_ENTITY = "entity does not exists, but was found";
    private static final String SCORE = "score";
    private static final String ID = "_id";

    private static final UnaryOperator<FindIterable<Document>> NO_PAGING = x -> x;

    private final MongoCollection<Document> collection;
    private final Datastore datastore;
    private final Class<T> type;

    BaseMongoRepository(Datastore datastore, Class<T> type) {
        this.collection = datastore.getDatabase().getCollection(getCollectionName(type));
        this.datastore = datastore;
        this.type = type;
    }

    Opt<T> findById(ObjectId id) {
        // TODO убрать dev.morphia.query.filters. путем вынесения фильтров в отдельный класс
        // TODO + ссылки на поля классов для фильтрации
        return Opt.ofNullable(datastore.find(type).filter(dev.morphia.query.filters.Filters.eq(ID, id)).first());
    }

    List<T> findAllByIds(List<ObjectId> ids) {
        return datastore.find(type)
                .filter(dev.morphia.query.filters.Filters.in(ID, ids))
                .stream()
                .toList();
    }

    List<T> find(Filter filter) {
        return datastore.find(type).filter(filter).stream().toList();
    }

    void save(T entity) {
        datastore.save(entity);
    }

    void save(List<T> entities) {
        datastore.save(entities);
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

    List<T> findByText(String text) {
        return findEntities(text, NO_PAGING);
    }

    MongoTextSearchResult<T> findByText(String text, int page, int pageSize) {
        return new MongoTextSearchResult<>(
                findEntities(text, found -> found.skip(page * pageSize).limit(pageSize)),
                collection.countDocuments(Filters.text(text))
        );
    }

    private String getCollectionName(Class<T> type) {
        Entity annotation = type.getAnnotation(Entity.class);
        if (annotation == null)
            throw new IllegalArgumentException(
                    "Entity class " + type.getSimpleName() + " must have @Entity annotation"
            );
        return annotation.value();
    }

    private List<T> findEntities(String text, UnaryOperator<FindIterable<Document>> paging) {
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

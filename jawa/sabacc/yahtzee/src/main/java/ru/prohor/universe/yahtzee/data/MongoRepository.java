package ru.prohor.universe.yahtzee.data;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filter;
import dev.morphia.query.updates.UpdateOperator;
import org.bson.Document;
import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.collections.Opt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class MongoRepository<T> {
    private static final String NO_ENTITY = "entity does not exists, but was found";
    private static final String SCORE = "score";
    private static final String ID = "_id";

    private static final UnaryOperator<FindIterable<Document>> NO_PAGING = x -> x;

    private final MongoCollection<Document> collection;
    private final Datastore datastore;
    private final Class<T> type;

    public MongoRepository(Datastore datastore, Class<T> type, String collection) {
        this.collection = datastore.getDatabase().getCollection(collection);
        this.datastore = datastore;
        this.type = type;
    }

    public Opt<T> findById(ObjectId id) {
        // TODO убрать dev.morphia.query.filters. путем вынесения фильтров в отдельный класс
        // TODO + ссылки на поля классов для фильтрации
        return Opt.ofNullable(datastore.find(type).filter(dev.morphia.query.filters.Filters.eq(ID, id)).first());
    }

    public void save(T entity) {
        datastore.save(entity);
    }

    public void save(List<T> entities) {
        datastore.save(entities);
    }

    public List<T> find(Filter filter) {
        return datastore.find(type).filter(filter).stream().toList();
    }

    @SuppressWarnings("all")
    public void update(Filter filter, UpdateOperator updateOperator) {
        datastore.find(type)
                .filter(filter)
                .update(updateOperator).execute();
    }

    public List<T> findByText(String text) {
        return findEntities(text, NO_PAGING);
    }

    public MongoTextSearchResult<T> findByText(String text, int page, int pageSize) {
        return new MongoTextSearchResult<>(
                findEntities(text, found -> found.skip((page - 1) * pageSize).limit(pageSize)),
                collection.countDocuments(Filters.text(text))
        );
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

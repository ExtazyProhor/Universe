package ru.prohor.universe.jocasta.jackson.morphia;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;
import ru.prohor.universe.jocasta.core.functional.DiPredicate;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.jackson.core.JocastaCoreModule;
import ru.prohor.universe.jocasta.morphia.impl.MongoInMemoryRepository;

import java.io.File;
import java.util.List;

public class MongoFileRepository<T> extends MongoInMemoryRepository<T> {
    private final File collectionStorageFile;
    private final ObjectMapper objectMapper;

    public MongoFileRepository(
            MonoFunction<T, ObjectId> idExtractor,
            Class<T> type,
            String collectionStorageFileName
    ) {
        this(idExtractor, null, type, collectionStorageFileName);
    }

    public MongoFileRepository(
            MonoFunction<T, ObjectId> idExtractor,
            DiPredicate<T, String> textSearchPredicate,
            Class<T> type,
            String collectionStorageFileName
    ) {
        super(idExtractor, textSearchPredicate, type);
        this.collectionStorageFile = new File(collectionStorageFileName);
        this.objectMapper = new ObjectMapper()
                .registerModule(JacksonMorphiaConfiguration.createMorphiaModule())
                .registerModule(new JocastaCoreModule());

        if (!collectionStorageFile.exists()) {
            return;
        }
        List<T> list = Sneaky.execute(
                () -> objectMapper.readValue(
                        collectionStorageFile,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, type)
                )
        );
        save(list);
    }

    @Override
    public void save(T entity) {
        super.save(entity);
        persist();
    }

    @Override
    public void save(List<T> entities) {
        super.save(entities);
        persist();
    }

    @Override
    public Opt<T> deleteById(ObjectId id) {
        Opt<T> deleted = super.deleteById(id);
        persist();
        return deleted;
    }

    @Override
    public long deleteAll() {
        long count = super.deleteAll();
        persist();
        return count;
    }

    private void persist() {
        File parent = collectionStorageFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new RuntimeException("parent directory of collection file does not exists: " + parent);
        }
        List<T> collection = findAll();
        Sneaky.execute(() -> objectMapper.writeValue(collectionStorageFile, collection));
    }
}

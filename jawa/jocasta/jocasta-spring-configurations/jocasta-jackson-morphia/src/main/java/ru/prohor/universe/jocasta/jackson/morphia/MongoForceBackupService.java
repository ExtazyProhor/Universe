package ru.prohor.universe.jocasta.jackson.morphia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.CollectionType;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.spring.features.PrettyJsonPrinter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MongoForceBackupService {
    private final Map<String, MongoRepository<? extends MongoEntityPojo<?>>> repositories;
    private final ObjectMapper objectMapper;
    private final ObjectWriter prettyWriter;

    public MongoForceBackupService(
            List<MongoRepository<? extends MongoEntityPojo<?>>> repositories,
            PrettyJsonPrinter prettyJsonPrinter,
            ObjectMapper objectMapper
    ) {
        this.repositories = repositories.stream().collect(Collectors.toMap(
                repository -> repository.type().getSimpleName(),
                MonoFunction.identity()
        ));
        this.objectMapper = objectMapper;
        this.prettyWriter = objectMapper.writer(prettyJsonPrinter);
    }

    public Map<String, List<?>> backupAsObjects() {
        return repositories.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().findAll()
        ));
    }

    public Opt<String> backupAsJson() {
        return Opt.tryOrNull(() -> objectMapper.writeValueAsString(backupAsObjects()));
    }

    public Opt<String> backupAsPrettyJson() {
        return Opt.tryOrNull(() -> prettyWriter.writeValueAsString(backupAsObjects()));
    }

    public void recovery(String jsonBackup) {
        JsonNode rootNode = Sneaky.execute(() -> objectMapper.readTree(jsonBackup));
        rootNode.properties().forEach(entry -> {
            String type = entry.getKey();
            JsonNode entities = entry.getValue();
            MongoRepository<?> repository = repositories.get(type);
            if (repository == null || !entities.isArray()) {
                return;
            }
            saveUnchecked(repository, entities);
        });
    }

    @SuppressWarnings("unchecked")
    private <T extends MongoEntityPojo<?>> void saveUnchecked(
            MongoRepository<?> repository,
            JsonNode entities
    ) {
        saveEntities((MongoRepository<T>) repository, entities);
    }

    private <T extends MongoEntityPojo<?>> void saveEntities(
            MongoRepository<T> repository,
            JsonNode entities
    ) {
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, repository.type());
        List<T> list = Sneaky.execute(() -> objectMapper.readerFor(listType).readValue(entities));
        repository.save(list);
    }
}

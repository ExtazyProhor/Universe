package ru.prohor.universe.jocasta.jackson.morphia;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
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
}

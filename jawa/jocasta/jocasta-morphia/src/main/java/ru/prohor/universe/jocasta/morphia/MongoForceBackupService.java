package ru.prohor.universe.jocasta.morphia;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MongoForceBackupService {
    private final List<MongoRepository<? extends MongoEntityPojo<?>>> repositories;

    public MongoForceBackupService(List<MongoRepository<? extends MongoEntityPojo<?>>> repositories) {
        this.repositories = repositories;
    }

    public Map<String, List<?>> backup() {
        return repositories.stream().collect(Collectors.toMap(
                repository -> repository.type().getSimpleName(),
                MongoRepository::findAll

        ));
    }
}

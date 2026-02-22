package ru.prohor.universe.jocasta.cfg.morphia;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.morphia.MongoForceBackupService;

@Configuration
@Import({
        MongoForceBackupService.class,
})
public class MongoSharedConfiguration {}

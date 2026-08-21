package ru.prohor.universe.jocasta.morphia.jackson;

import com.fasterxml.jackson.databind.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.core.jackson.JacksonConfiguration;

@Configuration
@Import({
        JacksonConfiguration.class,
        MongoForceBackupService.class
})
public class JacksonMorphiaConfiguration {
    @Bean
    public Module morphiaModule() {
        return createMorphiaModule();
    }

    public static Module createMorphiaModule() {
        return new JacksonMorphiaModule();
    }
}

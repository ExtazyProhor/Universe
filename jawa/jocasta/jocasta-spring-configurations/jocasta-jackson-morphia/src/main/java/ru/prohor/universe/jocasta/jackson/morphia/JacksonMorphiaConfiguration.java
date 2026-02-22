package ru.prohor.universe.jocasta.jackson.morphia;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.spring.configuration.JacksonConfiguration;

@Configuration
@Import(JacksonConfiguration.class)
public class JacksonMorphiaConfiguration {
    @Bean
    public Module morphiaModule() {
        SimpleModule morphiaModule = new SimpleModule();
        morphiaModule.addSerializer(ObjectId.class, new ObjectIdSerializer());
        morphiaModule.addDeserializer(ObjectId.class, new ObjectIdDeserializer());
        return morphiaModule;
    }
}

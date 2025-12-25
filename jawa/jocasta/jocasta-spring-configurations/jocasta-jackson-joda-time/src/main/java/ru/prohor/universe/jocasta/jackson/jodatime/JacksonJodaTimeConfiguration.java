package ru.prohor.universe.jocasta.jackson.jodatime;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.jackson.jodatime.deserializer.InstantDeserializer;
import ru.prohor.universe.jocasta.jackson.jodatime.deserializer.LocalDateDeserializer;
import ru.prohor.universe.jocasta.jackson.jodatime.serializer.InstantSerializer;
import ru.prohor.universe.jocasta.jackson.jodatime.serializer.LocalDateSerializer;
import ru.prohor.universe.jocasta.spring.configuration.JacksonConfiguration;

@Configuration
@Import(JacksonConfiguration.class)
public class JacksonJodaTimeConfiguration {
    @Bean
    public Module jodaModule() {
        SimpleModule jodaModule = new SimpleModule();

        jodaModule.addSerializer(Instant.class, new InstantSerializer());
        jodaModule.addSerializer(LocalDate.class, new LocalDateSerializer());

        jodaModule.addDeserializer(Instant.class, new InstantDeserializer());
        jodaModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());

        return jodaModule;
    }
}

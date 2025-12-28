package ru.prohor.universe.jocasta.jackson.jodatime;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.MonthDay;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.jackson.jodatime.deserializer.InstantDeserializer;
import ru.prohor.universe.jocasta.jackson.jodatime.deserializer.LocalDateDeserializer;
import ru.prohor.universe.jocasta.jackson.jodatime.deserializer.LocalTimeDeserializer;
import ru.prohor.universe.jocasta.jackson.jodatime.deserializer.MonthDayDeserializer;
import ru.prohor.universe.jocasta.jackson.jodatime.serializer.InstantSerializer;
import ru.prohor.universe.jocasta.jackson.jodatime.serializer.LocalDateSerializer;
import ru.prohor.universe.jocasta.jackson.jodatime.serializer.LocalTimeSerializer;
import ru.prohor.universe.jocasta.jackson.jodatime.serializer.MonthDaySerializer;
import ru.prohor.universe.jocasta.spring.configuration.JacksonConfiguration;

@Configuration
@Import(JacksonConfiguration.class)
public class JacksonJodaTimeConfiguration {
    @Bean
    public Module jodaModule() {
        SimpleModule jodaModule = new SimpleModule();

        jodaModule.addSerializer(Instant.class, new InstantSerializer());
        jodaModule.addSerializer(LocalDate.class, new LocalDateSerializer());
        jodaModule.addSerializer(LocalTime.class, new LocalTimeSerializer());
        jodaModule.addSerializer(MonthDay.class, new MonthDaySerializer());

        jodaModule.addDeserializer(Instant.class, new InstantDeserializer());
        jodaModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        jodaModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer());
        jodaModule.addDeserializer(MonthDay.class, new MonthDayDeserializer());

        return jodaModule;
    }
}

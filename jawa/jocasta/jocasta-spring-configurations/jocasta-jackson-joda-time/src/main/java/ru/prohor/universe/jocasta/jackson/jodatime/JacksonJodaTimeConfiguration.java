package ru.prohor.universe.jocasta.jackson.jodatime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.joda.time.Instant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class JacksonJodaTimeConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        SimpleModule jodaModule = new SimpleModule();

        jodaModule.addSerializer(
                Instant.class, new JsonSerializer<>() {
                    @Override
                    public void serialize(
                            Instant value, JsonGenerator gen, SerializerProvider serializers
                    ) throws IOException {
                        gen.writeNumber(value.getMillis() / 1000);
                    }
                }
        );

        jodaModule.addDeserializer(
                Instant.class, new JsonDeserializer<>() {
                    @Override
                    public Instant deserialize(JsonParser p, DeserializationContext context) throws IOException {
                        long unixSeconds = p.getValueAsLong();
                        return new Instant(unixSeconds * 1000);
                    }
                }
        );

        return new ObjectMapper().registerModule(jodaModule);
    }
}

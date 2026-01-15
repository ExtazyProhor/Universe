package ru.prohor.universe.jocasta.spring.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class JacksonConfiguration {
    @Bean
    public ObjectMapper objectMapper(List<Module> modules) {
        return new ObjectMapper()
                .registerModules(modules)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setDefaultPropertyInclusion(
                        JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL)
                )
                .setDefaultPropertyInclusion(
                        JsonInclude.Value.construct(JsonInclude.Include.NON_ABSENT, JsonInclude.Include.NON_ABSENT)
                );
    }
}

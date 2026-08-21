package ru.prohor.universe.jocasta.core.jackson;

import com.fasterxml.jackson.databind.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(JacksonConfiguration.class)
public class JacksonJocastaCoreConfiguration {
    @Bean
    public Module jocastaCoreModule() {
        return new JocastaCoreModule();
    }
}

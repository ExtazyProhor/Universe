package ru.prohor.universe.jocasta.jackson.jodatime;

import com.fasterxml.jackson.databind.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.spring.configuration.JacksonConfiguration;

@Configuration
@Import(JacksonConfiguration.class)
public class JacksonJocastaCoreConfiguration {
    @Bean
    public Module jocastaCoreModule() {
        return new JocastaCoreModule();
    }
}

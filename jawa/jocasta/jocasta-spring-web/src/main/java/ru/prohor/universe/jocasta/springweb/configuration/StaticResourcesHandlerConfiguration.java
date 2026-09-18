package ru.prohor.universe.jocasta.springweb.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prohor.universe.jocasta.springweb.StaticResourcesHandler;

import java.time.Duration;

@Configuration
public class StaticResourcesHandlerConfiguration {
    @Bean
    public StaticResourcesHandler staticResourcesHandler(
            @Value("${universe.jocasta.spring.files-cache-max-age}") Duration filesCacheMaxAge
    ) {
        return new StaticResourcesHandler(filesCacheMaxAge);
    }
}

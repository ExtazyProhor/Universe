package ru.prohor.universe.jocasta.spring.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prohor.universe.jocasta.spring.StaticResourcesHandler;

@Configuration
public class StaticResourcesHandlerConfiguration {
    @Bean
    public StaticResourcesHandler staticResourcesHandler(
            @Value("${universe.jocasta.spring.filesCacheMaxAgeDays}") int filesCacheMaxAgeDays
    ) {
        return new StaticResourcesHandler(filesCacheMaxAgeDays);
    }
}

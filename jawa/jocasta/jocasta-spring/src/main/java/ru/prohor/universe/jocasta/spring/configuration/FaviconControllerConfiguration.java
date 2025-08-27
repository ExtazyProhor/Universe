package ru.prohor.universe.jocasta.spring.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prohor.universe.jocasta.spring.StaticResourcesHandler;
import ru.prohor.universe.jocasta.spring.controllers.FaviconController;

@Configuration
public class FaviconControllerConfiguration {
    @Bean
    public FaviconController faviconController(
            StaticResourcesHandler staticResourcesHandler,
            @Value("${universe.jocasta.spring.favicons-path}") String pathToFaviconsDirectory
    ) {
        return new FaviconController(
                staticResourcesHandler,
                pathToFaviconsDirectory
        );
    }
}

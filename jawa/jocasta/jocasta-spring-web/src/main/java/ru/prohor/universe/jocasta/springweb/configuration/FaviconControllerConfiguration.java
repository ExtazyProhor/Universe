package ru.prohor.universe.jocasta.springweb.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prohor.universe.jocasta.springweb.StaticResourcesHandler;
import ru.prohor.universe.jocasta.springweb.controllers.FaviconController;

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

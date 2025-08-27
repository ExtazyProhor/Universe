package ru.prohor.universe.jocasta.spring.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prohor.universe.jocasta.spring.StaticResourcesHandler;
import ru.prohor.universe.jocasta.spring.controllers.FilesController;

@Configuration
public class FilesControllerConfiguration {
    @Bean
    public FilesController filesController(
            StaticResourcesHandler staticResourcesHandler,
            @Value("${universe.spring.root}") String root,
            @Value("${universe.spring.redefinedRoot:#{null}}") String redefinedRoot
    ) {
        return new FilesController(
                staticResourcesHandler,
                root,
                redefinedRoot
        );
    }
}

package ru.prohor.universe.jocasta.springweb.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prohor.universe.jocasta.springweb.StaticResourcesHandler;
import ru.prohor.universe.jocasta.springweb.controllers.FilesController;

@Configuration
public class FilesControllerConfiguration {
    @Bean
    public FilesController filesController(
            StaticResourcesHandler staticResourcesHandler,
            @Value("${universe.jocasta.spring.root}") String root,
            @Value("${universe.jocasta.spring.redefinedRoot:#{null}}") String redefinedRoot
    ) {
        return new FilesController(
                staticResourcesHandler,
                root,
                redefinedRoot
        );
    }
}

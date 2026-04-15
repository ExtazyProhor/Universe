package ru.prohor.universe.jocasta.springweb.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.springweb.StaticResourcesHandler;
import ru.prohor.universe.jocasta.springweb.controllers.FilesController;

@Configuration
@Import(StaticResourcesHandlerConfiguration.class)
public class FilesControllerConfiguration {
    @Bean
    public FilesController filesController(
            StaticResourcesHandler staticResourcesHandler,
            @Value("${universe.jocasta.spring.root}") String root
    ) {
        return new FilesController(
                staticResourcesHandler,
                root
        );
    }
}

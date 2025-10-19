package ru.prohor.universe.jocasta.springweb.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prohor.universe.jocasta.springweb.StaticResourcesHandler;
import ru.prohor.universe.jocasta.springweb.controllers.RootController;

@Configuration
public class RootControllerConfiguration {
    @Bean
    public RootController rootController(
            StaticResourcesHandler staticResourcesHandler,
            @Value("${universe.jocasta.spring.root-file}") String file,
            @Value("${universe.jocasta.spring.root-dir}") String directory
    ) {
        return new RootController(staticResourcesHandler, file, directory);
    }
}

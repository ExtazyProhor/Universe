package ru.prohor.universe.jocasta.springweb.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        StaticResourcesHandlerConfiguration.class,
        FilesControllerConfiguration.class,
        FaviconControllerConfiguration.class,
        PagesControllerConfiguration.class,
})
public class AllControllersConfiguration {}

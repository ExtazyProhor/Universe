package ru.prohor.universe.jocasta.springweb.configuration;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import ru.prohor.universe.jocasta.springweb.StaticResourcesHandler;
import ru.prohor.universe.jocasta.springweb.controllers.PagesController;

@Configuration
public class PagesControllerConfiguration {
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final PagesController pagesController;
    private final String paths;

    public PagesControllerConfiguration(
            RequestMappingHandlerMapping requestMappingHandlerMapping,
            StaticResourcesHandler staticResourcesHandler,
            @Value("${universe.jocasta.spring.pages-dir}") String pagesDirectory,
            @Value("${universe.jocasta.spring.pages-list}") String paths
    ) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.pagesController = new PagesController(staticResourcesHandler, pagesDirectory);
        this.paths = paths;
    }

    @PostConstruct
    public void init() {
        // TODO лучше руками поискать какие файлы html есть в папке, вместо ручного указания
        RequestMappingInfo requestMappingInfo = RequestMappingInfo
                .paths(paths.split(","))
                .methods(RequestMethod.GET)
                .build();
        try {
            requestMappingHandlerMapping.registerMapping(
                    requestMappingInfo,
                    pagesController,
                    PagesController.class.getMethod("fromRequest", HttpServletRequest.class)
            );
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public PagesController pagesController() {
        return pagesController;
    }
}

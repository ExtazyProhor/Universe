package ru.prohor.universe.jocasta.springweb.configuration;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.springweb.StaticResourcesHandler;
import ru.prohor.universe.jocasta.springweb.controllers.PagesController;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

@Configuration
@Import(StaticResourcesHandlerConfiguration.class)
public class PagesControllerConfiguration {
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final PagesController pagesController;
    private final String pagesDirectory;

    public PagesControllerConfiguration(
            RequestMappingHandlerMapping requestMappingHandlerMapping,
            StaticResourcesHandler staticResourcesHandler,
            @Value("${universe.jocasta.spring.pages-dir}") String pagesDirectory
    ) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.pagesController = new PagesController(staticResourcesHandler, pagesDirectory);
        this.pagesDirectory = pagesDirectory;
    }

    @PostConstruct
    public void init() throws Exception {
        String[] paths = Opt.ofNullable(new File(pagesDirectory).list())
                .map(Arrays::stream)
                .orElseThrow(() -> new FileNotFoundException("Directory with pages does not exist: " + pagesDirectory))
                .filter(file -> file.endsWith(PagesController.HTML))
                .map(file -> "/" + file.substring(0, file.length() - PagesController.HTML.length()))
                .toArray(String[]::new);

        RequestMappingInfo requestMappingInfo = RequestMappingInfo
                .paths(paths)
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

package ru.prohor.universe.jocasta.springweb.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.prohor.universe.jocasta.springweb.ControllersUtils;
import ru.prohor.universe.jocasta.springweb.StaticResourcesHandler;

import java.nio.file.Path;

@Component
public class PagesController {
    private static final String HTML = ".html";

    private final StaticResourcesHandler staticResourcesHandler;
    private final Path pagesPath;

    public PagesController(
            StaticResourcesHandler staticResourcesHandler,
            String pagesDirectoryPath
    ) {
        this.staticResourcesHandler = staticResourcesHandler;
        this.pagesPath = Path.of(pagesDirectoryPath);
    }

    public ResponseEntity<FileSystemResource> fromRequest(HttpServletRequest request) {
        String path = ControllersUtils.getPathWithoutSlashes(request);
        return staticResourcesHandler.getResource(pagesPath.resolve(path + HTML));
    }
}

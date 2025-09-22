package ru.prohor.universe.jocasta.springweb.controllers;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.prohor.universe.jocasta.springweb.StaticResourcesHandler;

import java.nio.file.Path;

@Controller
public class FaviconController {
    private static final String FAVICON_FILE = "favicon.ico";
    private static final String FAVICON_PATH = "/" + FAVICON_FILE;

    private final StaticResourcesHandler staticResourcesHandler;
    private final Path faviconsDirectory;

    public FaviconController(
            StaticResourcesHandler staticResourcesHandler,
            String pathToFaviconsDirectory
    ) {
        this.staticResourcesHandler = staticResourcesHandler;
        this.faviconsDirectory = Path.of(pathToFaviconsDirectory);
    }

    @GetMapping(FAVICON_PATH) // TODO иконки для других платформ
    public ResponseEntity<FileSystemResource> favicon() {
        return staticResourcesHandler.getResource(faviconsDirectory.resolve(FAVICON_FILE));
    }
}

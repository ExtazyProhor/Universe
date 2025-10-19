package ru.prohor.universe.jocasta.springweb.controllers;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.prohor.universe.jocasta.springweb.StaticResourcesHandler;

import java.nio.file.Path;

@Controller
public class RootController {
    private final StaticResourcesHandler staticResourcesHandler;
    private final String indexFileName;
    private final Path directory;

    public RootController(
            StaticResourcesHandler staticResourcesHandler,
            String indexFileName,
            String pathToFaviconsDirectory
    ) {
        this.staticResourcesHandler = staticResourcesHandler;
        this.indexFileName = indexFileName;
        this.directory = Path.of(pathToFaviconsDirectory);
    }

    @GetMapping("/")
    public ResponseEntity<FileSystemResource> root() {
        return staticResourcesHandler.getResource(directory.resolve(indexFileName));
    }
}

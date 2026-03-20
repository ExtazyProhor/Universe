package ru.prohor.universe.hyperspace.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.prohor.universe.jocasta.springweb.StaticResourcesHandler;

import java.nio.file.Path;

@Controller
@RequestMapping("/requests.js")
public class UniverseRequestsController {
    private final StaticResourcesHandler staticResourcesHandler;
    private final String requestsPath;

    public UniverseRequestsController(
            StaticResourcesHandler staticResourcesHandler,
            @Value("${universe.scarif.jwt.requests-path}")
            String requestsPath
    ) {
        this.staticResourcesHandler = staticResourcesHandler;
        this.requestsPath = requestsPath;
    }

    @GetMapping
    public ResponseEntity<FileSystemResource> requests() {
        return staticResourcesHandler.getResource(Path.of(requestsPath));
    }
}

package ru.prohor.universe.jocasta.springweb.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.prohor.universe.jocasta.springweb.ControllersUtils;
import ru.prohor.universe.jocasta.springweb.StaticResourcesHandler;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

@Controller
public class FilesController {
    private final StaticResourcesHandler staticResourcesHandler;
    private final Path root;

    public FilesController(
            StaticResourcesHandler staticResourcesHandler,
            String root
    ) {
        this.staticResourcesHandler = staticResourcesHandler;
        this.root = Path.of(root).normalize();
    }

    @GetMapping("/files/**")
    public ResponseEntity<FileSystemResource> files(HttpServletRequest request) {
        String path = ControllersUtils.getPathWithoutSlashes(request);
        path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        try {
            Path file = root.resolve(path).normalize();
            if (!file.startsWith(root)) {
                return ResponseEntity.badRequest().build();
            }
            return staticResourcesHandler.getResource(file);
        } catch (InvalidPathException e) {
            // TODO log.warn
            return ResponseEntity.badRequest().build();
        }
    }
}

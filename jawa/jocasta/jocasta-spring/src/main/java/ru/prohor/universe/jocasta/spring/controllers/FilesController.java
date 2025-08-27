package ru.prohor.universe.jocasta.spring.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.prohor.universe.jocasta.core.collections.Opt;
import ru.prohor.universe.jocasta.spring.ControllersUtils;
import ru.prohor.universe.jocasta.spring.StaticResourcesHandler;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

@Controller
public class FilesController {
    private final StaticResourcesHandler staticResourcesHandler;
    private final Opt<Path> redefinedRoot;
    private final Path root;

    public FilesController(
            StaticResourcesHandler staticResourcesHandler,
            String root,
            String redefinedRoot
    ) {
        this.staticResourcesHandler = staticResourcesHandler;
        this.root = Path.of(root).normalize();
        this.redefinedRoot = Opt.ofNullable(redefinedRoot).map(Path::of);
    }

    @GetMapping("/files/**")
    public ResponseEntity<FileSystemResource> files(HttpServletRequest request) {
        String path = ControllersUtils.getPathWithoutSlashes(request);
        try {
            if (redefinedRoot.isPresent()) {
                Opt<Path> redefinedFileO = resolveOrBadRequest(redefinedRoot.get(), path);
                if (redefinedFileO.isEmpty())
                    return ResponseEntity.badRequest().build();
                Path redefinedFile = redefinedFileO.get();
                if (Files.exists(redefinedFile) && Files.isRegularFile(redefinedFile))
                    return staticResourcesHandler.getResource(redefinedFile);
            }

            Opt<Path> file = resolveOrBadRequest(root, path);
            if (file.isEmpty())
                return ResponseEntity.badRequest().build();
            return staticResourcesHandler.getResource(file.get());
        } catch (InvalidPathException e) {
            // TODO log.warn
            return ResponseEntity.badRequest().build();
        }
    }

    private static Opt<Path> resolveOrBadRequest(Path root, String path) {
        Path file = root.resolve(path).normalize();
        return Opt.when(file.startsWith(root), file);
    }
}

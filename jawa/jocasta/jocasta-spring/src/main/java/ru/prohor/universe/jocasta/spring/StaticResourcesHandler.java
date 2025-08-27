package ru.prohor.universe.jocasta.spring;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import ru.prohor.universe.jocasta.core.collections.Opt;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class StaticResourcesHandler {
    private final int filesCacheMaxAgeDays;

    public StaticResourcesHandler(int filesCacheMaxAgeDays) {
        this.filesCacheMaxAgeDays = filesCacheMaxAgeDays;
    }

    public ResponseEntity<FileSystemResource> getResource(Path path) {
        FileSystemResource resource = new FileSystemResource(path);
        if (!resource.exists() || !resource.isReadable())
            return ResponseEntity.notFound().build(); // TODO сделать механизм для 404

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(filesCacheMaxAgeDays, TimeUnit.DAYS).cachePublic().mustRevalidate())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path.getFileName() + '"')
                .contentType(MediaType.parseMediaType(Opt.tryOrNull(
                        () -> Files.probeContentType(path)
                ).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE)))
                .body(resource);
    }
}

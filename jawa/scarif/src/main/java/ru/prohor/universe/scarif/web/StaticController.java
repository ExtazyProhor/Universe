package ru.prohor.universe.scarif.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import ru.prohor.universe.jocasta.core.collections.Opt;
import ru.prohor.universe.scarif.services.CookieProvider;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class StaticController {
    private static final String HTML = ".html";

    private final CookieProvider cookieProvider;
    private final Opt<Path> customRequestsPath;
    private final Path root;
    private final Path pagesRoot;
    private final Path index;
    private final Path favicon;
    private final int filesCacheMaxAgeDays;

    public StaticController(
            CookieProvider cookieProvider,
            String requestsPath,
            String root,
            String index,
            int filesCacheMaxAgeDays
    ) {
        this.cookieProvider = cookieProvider;
        this.customRequestsPath = Opt.ofNullable(requestsPath).map(Path::of);
        this.root = Path.of(root).normalize();
        this.pagesRoot = this.root.resolve("pages").normalize();
        this.index = this.pagesRoot.resolve(index + HTML).normalize();
        this.favicon = this.pagesRoot.resolve("images/favicon.ico").normalize();
        this.filesCacheMaxAgeDays = filesCacheMaxAgeDays;
    }

    @GetMapping("/")
    public ResponseEntity<FileSystemResource> root() {
        return getResource(index);
    }

    @GetMapping("/favicon.ico") // TODO иконки для других платформ
    public ResponseEntity<FileSystemResource> favicon() {
        return getResource(favicon);
    }

    @GetMapping("/pages/**")
    public ResponseEntity<FileSystemResource> pages(HttpServletRequest request) {
        String path = removeSlashes(request.getRequestURI().substring(request.getContextPath().length()));
        try {
            Path file = root.resolve(path).normalize();
            if (!file.startsWith(pagesRoot))
                return ResponseEntity.badRequest().build();

            if (!Files.exists(file) || !Files.isRegularFile(file)) {
                file = root.resolve(path + HTML).normalize();
                if (!Files.exists(file) || !Files.isRegularFile(file))
                    return ResponseEntity.notFound().build();
            }
            if (customRequestsPath.isPresent() && path.equals("pages/js/requests.js"))
                return getResource(customRequestsPath.get());
            return getResource(file);

        } catch (InvalidPathException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/local-logout") // TODO
    public ResponseEntity<?> localLogout() {
        return fromCookies(List.of(
                cookieProvider.clearRefreshCookie(),
                cookieProvider.clearAccessCookie()
        ));
    }

    private ResponseEntity<?> fromCookies(List<String> cookies) { // TODO REMOVE DUPLICATE
        return ResponseEntity.ok().headers(
                new HttpHeaders(MultiValueMap.fromMultiValue(Map.of(
                        HttpHeaders.SET_COOKIE,
                        cookies
                )))
        ).build();
    }

    private ResponseEntity<FileSystemResource> getResource(Path path) {
        FileSystemResource resource = new FileSystemResource(path);
        if (!resource.exists() || !resource.isReadable())
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(filesCacheMaxAgeDays, TimeUnit.DAYS).cachePublic().mustRevalidate())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path.getFileName() + '"')
                .contentType(MediaType.parseMediaType(Opt.tryOrNull(
                        () -> Files.probeContentType(path)
                ).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE)))
                .body(resource);
    }

    private String removeSlashes(String s) {
        if (s.endsWith("/"))
            s = s.substring(0, s.length() - 1);
        if (s.startsWith("/"))
            s = s.substring(1);
        return s;
    }
}

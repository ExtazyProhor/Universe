package ru.prohor.universe.jocasta.springweb.controllers;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.prohor.universe.jocasta.springweb.StaticResourcesHandler;

import java.nio.file.Path;

@Controller
public class FaviconController {
    private static final String FAVICON_ICO = "favicon.ico";
    private static final String FAVICON_SVG = "favicon.svg";
    private static final String APPLE_TOUCH_ICON_PNG = "apple-touch-icon.png";
    private static final String FAVICON_96_X_96_PNG = "favicon-96x96.png";
    private static final String SITE_WEBMANIFEST = "site.webmanifest";
    private static final String WEB_APP_MANIFEST_512_X_512_PNG = "web-app-manifest-512x512.png";
    private static final String WEB_APP_MANIFEST_192_X_192_PNG = "web-app-manifest-192x192.png";

    private final StaticResourcesHandler staticResourcesHandler;
    private final Path faviconsDirectory;

    public FaviconController(
            StaticResourcesHandler staticResourcesHandler,
            String pathToFaviconsDirectory
    ) {
        this.staticResourcesHandler = staticResourcesHandler;
        this.faviconsDirectory = Path.of(pathToFaviconsDirectory);
    }

    // TODO одна ручка, которая сама собирает файлы
    @GetMapping("/" + FAVICON_ICO)
    public ResponseEntity<FileSystemResource> faviconIco() {
        return staticResourcesHandler.getResource(faviconsDirectory.resolve(FAVICON_ICO));
    }

    @GetMapping("/" + FAVICON_SVG)
    public ResponseEntity<FileSystemResource> faviconSvg() {
        return staticResourcesHandler.getResource(faviconsDirectory.resolve(FAVICON_SVG));
    }

    @GetMapping("/" + APPLE_TOUCH_ICON_PNG)
    public ResponseEntity<FileSystemResource> appleTouch() {
        return staticResourcesHandler.getResource(faviconsDirectory.resolve(APPLE_TOUCH_ICON_PNG));
    }

    @GetMapping("/" + FAVICON_96_X_96_PNG)
    public ResponseEntity<FileSystemResource> favicon96x96() {
        return staticResourcesHandler.getResource(faviconsDirectory.resolve(FAVICON_96_X_96_PNG));
    }

    @GetMapping("/" + SITE_WEBMANIFEST)
    public ResponseEntity<FileSystemResource> siteWebmanifest() {
        return staticResourcesHandler.getResource(faviconsDirectory.resolve(SITE_WEBMANIFEST));
    }

    @GetMapping("/" + WEB_APP_MANIFEST_512_X_512_PNG)
    public ResponseEntity<FileSystemResource> manifest512x512() {
        return staticResourcesHandler.getResource(faviconsDirectory.resolve(WEB_APP_MANIFEST_512_X_512_PNG));
    }

    @GetMapping("/" + WEB_APP_MANIFEST_192_X_192_PNG)
    public ResponseEntity<FileSystemResource> manifest192x192() {
        return staticResourcesHandler.getResource(faviconsDirectory.resolve(WEB_APP_MANIFEST_192_X_192_PNG));
    }
}

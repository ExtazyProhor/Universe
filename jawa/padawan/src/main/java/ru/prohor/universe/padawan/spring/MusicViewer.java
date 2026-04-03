package ru.prohor.universe.padawan.spring;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;
import ru.prohor.universe.jocasta.spring.features.PrettyJsonPrinter;
import ru.prohor.universe.jocasta.springweb.StaticResourcesHandler;
import ru.prohor.universe.padawan.Padawan;
import ru.prohor.universe.padawan.TestFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MusicViewer {
    private static List<Page> getPages() {
        return read()
                .stream()
                .collect(Collectors.groupingBy(
                        model -> model.name.toLowerCase(),
                        Collectors.toList()
                ))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() != 1)
                .sorted(Comparator.comparingInt(
                        e -> e.getValue().stream().mapToInt(Model::position).min().orElse(0)
                ))
                .map(e -> new Page(
                        e.getKey(),
                        e.getValue().stream().map(model -> new Track(
                                model.name,
                                model.authors,
                                model.track
                        )).toList()
                )).toList();
    }

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final ObjectWriter writer = mapper.writer(new PrettyJsonPrinter());

    private static List<Model> read() {
        return Sneaky.execute(() -> mapper.readValue(Padawan.read(TestFile.JSON), new TypeReference<>() {}));
    }

    private static void write(List<Model> models) {
        Sneaky.execute(() -> Padawan.write(TestFile.JSON, writer.writeValueAsString(models)));
    }

    private record Model(
            int position,
            String name,
            List<String> authors,
            String track
    ) {}

    private static final Path PATH = Path.of("/music");

    private static ResponseEntity<FileSystemResource> getTrack(
            StaticResourcesHandler staticResourcesHandler,
            String file
    ) {
        return staticResourcesHandler.getResource(PATH.resolve(file));
    }

    private static String getRootHtml() {
        return Sneaky.execute(() -> Files.readString(Path.of("padawan/src/main/resources/special/music-viewer.html")));
    }

    @RestController
    @RequestMapping("/music-viewer")
    public static class MusicController {
        private final StaticResourcesHandler staticResourcesHandler;

        public MusicController(StaticResourcesHandler staticResourcesHandler) {
            this.staticResourcesHandler = staticResourcesHandler;
        }

        @GetMapping("/pages")
        public List<Page> pages() {
            return getPages();
        }

        @GetMapping("/track/{file}")
        public ResponseEntity<FileSystemResource> track(
                @PathVariable("file") String file
        ) {
            return getTrack(staticResourcesHandler, file);
        }

        @GetMapping("/")
        public ResponseEntity<String> root() {
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(getRootHtml());
        }
    }

    public record Page(
            String title,
            List<Track> tracks
    ) {}

    public record Track(
            String title,
            List<String> authors,
            String file
    ) {}
}

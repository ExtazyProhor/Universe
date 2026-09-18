package ru.prohor.universe.padawan.scripts.parsers;

import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;
import ru.prohor.universe.jocasta.core.functional.MonoPredicate;
import ru.prohor.universe.jocasta.core.utils.FileSystemUtils;
import ru.prohor.universe.padawan.Padawan;
import ru.prohor.universe.padawan.TestFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AllFilesToOneMd {
    private static final Preset SCARIF_FRONT = new Preset(
            FileSystemUtils.userHome()
                    .asPath()
                    .resolve("universe/jawa/fondor/fondor-scarif/content")
                    .toString(),
            TestFile.OUTPUT,
            path -> !path.getFileName().toString().endsWith(".jpg") && !path.getFileName().toString().endsWith(".svg")
    );
    private static final Preset TOVARISCH_PROTO = new Preset(
            FileSystemUtils.userHome()
                    .asPath()
                    .resolve("arcadia/bdui/backend/tovarisch/v2/entity/src/main/kotlin/ru/yandex/tovarisch/core")
                    .toString(),
            TestFile.TXT,
            path -> path.getFileName().toString().endsWith(".kt")
    );

    static void main() throws IOException {
        process(SCARIF_FRONT);
    }

    private static void process(Preset preset) throws IOException {
        StringBuilder builder = new StringBuilder();
        Files.walk(Path.of(preset.directory)).filter(preset.filter).filter(Files::isRegularFile).forEach(path -> {
            Sneaky.execute(() -> {
                builder.append(path.getFileName()).append(":\n```").append(getFileExtension(path)).append("\n");
                String content;
                try {
                    content = Files.readString(path);
                } catch (Exception e) {
                    throw new RuntimeException("File " + path, e);
                }
                builder.append(content).append("\n```\n\n");
            });
        });
        Padawan.write(preset.output, builder.toString());
    }

    private static String getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot <= 0) {
            return "";
        }
        return fileName.substring(lastIndexOfDot + 1);
    }

    private record Preset(
            String directory,
            TestFile output,
            MonoPredicate<Path> filter
    ) {}
}

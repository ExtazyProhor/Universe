package ru.prohor.universe.padawan.scripts.parsers;

import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;
import ru.prohor.universe.jocasta.core.functional.MonoPredicate;
import ru.prohor.universe.padawan.Padawan;
import ru.prohor.universe.padawan.TestFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AllFilesToOneMd {
    public static void main(String[] args) throws IOException {
        process(
                ".",
                TestFile.OUTPUT,
                path -> true
        );
    }

    public static String getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot <= 0) {
            return "";
        }
        return fileName.substring(lastIndexOfDot + 1);
    }

    private static void process(String directory, TestFile output, MonoPredicate<Path> filter) throws IOException {
        StringBuilder builder = new StringBuilder();
        Files.walk(Path.of(directory)).filter(filter).filter(Files::isRegularFile).forEach(path -> {
            Sneaky.execute(() -> {
                builder.append(path.getFileName()).append(":\n```").append(getFileExtension(path)).append("\n");
                builder.append(Files.readString(path)).append("\n```\n\n");
            });
        });
        Padawan.write(output, builder.toString());
    }
}

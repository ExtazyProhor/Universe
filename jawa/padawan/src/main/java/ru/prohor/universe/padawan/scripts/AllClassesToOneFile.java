package ru.prohor.universe.padawan.scripts;

import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;
import ru.prohor.universe.padawan.Padawan;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AllClassesToOneFile {
    public static void main(String[] args) throws Exception {
        mergeJavaFiles(
                Path.of("."),
                Padawan.resolve("txt.txt"),
                path -> path.toString().endsWith(".java")
        );
    }

    public static void mergeJavaFiles(Path rootDir, Path outputFile, Predicate<Path> filter) {
        try (Stream<Path> paths = Files.walk(rootDir)) {
            Files.deleteIfExists(outputFile);
            paths.filter(Files::isRegularFile)
                    .filter(filter)
                    .forEach(path -> {
                        try {
                            Files.writeString(
                                    outputFile,
                                    Files.readString(path, StandardCharsets.UTF_8),
                                    StandardCharsets.UTF_8,
                                    StandardOpenOption.APPEND
                            );
                        } catch (IOException e) {
                            Sneaky.throwUnchecked(e);
                        }
                    });
        } catch (IOException e) {
            Sneaky.throwUnchecked(e);
        }
    }
}

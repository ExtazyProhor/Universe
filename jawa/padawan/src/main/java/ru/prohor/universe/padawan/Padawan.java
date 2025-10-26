package ru.prohor.universe.padawan;

import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Padawan {
    private static final Path RESOURCES = Path.of("padawan/src/main/resources");

    public static Path resolve(String path) {
        return RESOURCES.resolve(path);
    }

    public static String read(String path) {
        return Sneaky.execute(() -> Files.readString(RESOURCES.resolve(path)));
    }

    public static List<String> readLines(String path) {
        return Sneaky.execute(() -> Files.readAllLines(RESOURCES.resolve(path)));
    }

    public static void write(String path, String content) {
        Sneaky.execute(() -> Files.writeString(RESOURCES.resolve(path), content));
    }

    public static void writeLines(String path, List<String> lines) {
        Sneaky.execute(() -> Files.write(RESOURCES.resolve(path), lines));
    }
}

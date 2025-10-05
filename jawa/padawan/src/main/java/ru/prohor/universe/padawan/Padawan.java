package ru.prohor.universe.padawan;

import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Padawan {
    private static final Path RESOURCES = Path.of("padawan/src/main/resources");

    public static Path load(String path) {
        return RESOURCES.resolve(path);
    }

    public static String read(String path) {
        return Sneaky.execute(() -> Files.readString(RESOURCES.resolve(path)));
    }

    public static List<String> readLines(String path) {
        return Sneaky.execute(() -> Files.readAllLines(RESOURCES.resolve(path)));
    }
}

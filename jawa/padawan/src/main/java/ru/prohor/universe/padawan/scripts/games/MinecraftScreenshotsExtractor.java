package ru.prohor.universe.padawan.scripts.games;

import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class MinecraftScreenshotsExtractor {
    public static void main(String[] args) throws Exception {
        collectScreens(
                Path.of("C:\\Users\\User\\Downloads\\screens"),
                Path.of("C:\\Users\\User\\curseforge\\Instances")
        );
    }

    private static void collectScreens(
            Path destinationFolder,
            Path installationsDir
    ) {
        if (!Files.exists(destinationFolder))
            Sneaky.execute(() -> Files.createDirectory(destinationFolder));
        ls(installationsDir).forEach(instance -> {
            Path installationDir = instance.resolve("screenshots");
            ls(installationDir).forEach(
                    screenshot -> cp(
                            screenshot,
                            destinationFolder.resolve(screenshot.getFileName().toString())
                    )
            );
        });
    }

    private static List<Path> ls(Path dir) {
        return Sneaky.execute(() -> {
            try (Stream<Path> stream = Files.list(dir)) {
                return stream.toList();
            }
        });
    }

    private static void cp(Path src, Path trg) {
        Sneaky.execute(() -> Files.copy(src, trg));
    }
}

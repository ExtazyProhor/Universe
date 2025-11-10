package ru.prohor.universe.padawan.scripts.parsers;

import jakarta.annotation.Nonnull;
import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;
import ru.prohor.universe.jocasta.core.utils.FileSystemUtils;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class ObsidianStats {
    private static final Set<String> EXCLUDED = new HashSet<>(Set.of(
            ".git",
            ".obsidian",
            "assets",
            ".gitignore",
            ".DS_Store"
    ));

    public static void main(String[] args) {
        stats(FileSystemUtils.userHome().asPath().resolve("theory"));
    }

    private static void stats(Path storagePath) {
        System.out.println("full: " + getFullStats(storagePath));
        System.out.println();
        getFilesStats(storagePath).forEach(System.out::println);
    }

    private static List<FileStats> getFilesStats(Path storagePath) {
        List<FileStats> list = new ArrayList<>();
        fileTree(
                storagePath, path -> Sneaky.execute(() -> {
                    FileStats fileStats = new FileStats(path.getFileName().toString(), 0, 0);
                    Files.readAllLines(path).forEach(line -> {
                        fileStats.chars += line.length();
                        fileStats.lines++;
                    });
                    list.add(fileStats);
                })
        );
        list.sort(Comparator.reverseOrder());
        return list;
    }

    private static String getFullStats(Path storagePath) {
        FileStats fileStats = new FileStats(null, 0, 0);
        fileTree(
                storagePath, path -> Sneaky.execute(() -> Files.readAllLines(path).forEach(line -> {
                    fileStats.chars += line.length();
                    fileStats.lines++;
                }))
        );
        return "chars=" + fileStats.chars + ",\tlines=" + fileStats.lines;
    }

    private static void fileTree(Path storagePath, Consumer<Path> consumer) {
        Sneaky.execute(() -> {
            Files.walkFileTree(storagePath, new ObsidianStatsFileVisitor(consumer));
        });
    }

    private static class ObsidianStatsFileVisitor extends SimpleFileVisitor<Path> {
        private final Consumer<Path> consumer;

        public ObsidianStatsFileVisitor(Consumer<Path> consumer) {
            this.consumer = consumer;
        }

        @Override
        @Nonnull
        public FileVisitResult preVisitDirectory(
                @Nonnull Path dir,
                @Nonnull BasicFileAttributes attrs
        ) {
            if (EXCLUDED.contains(dir.getFileName().toString())) {
                return FileVisitResult.SKIP_SUBTREE;
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        @Nonnull
        public FileVisitResult visitFile(
                @Nonnull Path file,
                @Nonnull BasicFileAttributes attrs
        ) {
            if (EXCLUDED.contains(file.getFileName().toString()))
                return FileVisitResult.CONTINUE;
            if (Files.isRegularFile(file))
                consumer.accept(file);
            return FileVisitResult.CONTINUE;
        }
    }

    private static class FileStats implements Comparable<FileStats> {
        private final String fileName;
        private long chars;
        private long lines;

        private FileStats(String fileName, long chars, long lines) {
            this.fileName = fileName;
            this.chars = chars;
            this.lines = lines;
        }

        @Override
        public int compareTo(FileStats o) {
            return Long.compare(this.chars, o.chars);
        }

        @Override
        public String toString() {
            return fileName + ":" + " ".repeat(50 - fileName.length()) + "chars=" + chars + ",\tlines=" + lines;
        }
    }
}

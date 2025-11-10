package ru.prohor.universe.padawan.scripts.obsidian;

import jakarta.annotation.Nonnull;
import ru.prohor.universe.jocasta.core.functional.MonoPredicate;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

public class ObsidianAllNotesVisitor extends SimpleFileVisitor<Path> {
    private static final Set<String> EXCLUDED_FILTER = new HashSet<>(Set.of(
            ".git",
            ".obsidian",
            "assets",
            ".gitignore",
            ".DS_Store"
    ));

    private final MonoPredicate<Path> checker;

    public ObsidianAllNotesVisitor(MonoPredicate<Path> checker) {
        this.checker = checker;
    }

    @Override
    @Nonnull
    public FileVisitResult preVisitDirectory(
            @Nonnull Path dir,
            @Nonnull BasicFileAttributes attrs
    ) {
        if (EXCLUDED_FILTER.contains(dir.getFileName().toString()))
            return FileVisitResult.SKIP_SUBTREE;
        return FileVisitResult.CONTINUE;
    }

    @Override
    @Nonnull
    public FileVisitResult visitFile(
            @Nonnull Path file,
            @Nonnull BasicFileAttributes attrs
    ) {
        if (EXCLUDED_FILTER.contains(file.getFileName().toString()))
            return FileVisitResult.CONTINUE;
        if (Files.isRegularFile(file))
            if (!checker.test(file))
                throw new ObsidianLinterException();
        return FileVisitResult.CONTINUE;
    }
}

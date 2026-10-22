package ru.prohor.universe.jocasta.core.utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileSystemUtils {
    private FileSystemUtils() {}

    public static Dir userDir() {
        return new Dir(System.getProperty("user.dir"));
    }

    public static Dir userHome() {
        return new Dir(System.getProperty("user.home"));
    }

    public static Dir downloads() {
        return userHome().resolve("Downloads");
    }

    public static class Dir {
        private final String path;

        private Dir(String path) {
            this.path = path;
        }

        @NotNull
        public String asString() {
            return path;
        }

        @NotNull
        public Path asPath() {
            return Paths.get(path);
        }

        @NotNull
        public File asFile() {
            return new File(path);
        }

        @Override
        public String toString() {
            return asString();
        }

        public Dir resolve(String other) {
            return new Dir(asPath().resolve(other).toString());
        }
    }
}

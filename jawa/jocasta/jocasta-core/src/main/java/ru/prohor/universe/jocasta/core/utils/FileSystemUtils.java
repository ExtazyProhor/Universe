package ru.prohor.universe.jocasta.core.utils;

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

    public static class Dir {
        private final String path;

        public Dir(String path) {
            this.path = path;
        }

        public String asString() {
            return path;
        }

        public Path asPath() {
            return Paths.get(path);
        }

        public File asFile() {
            return new File(path);
        }

        @Override
        public String toString() {
            return asString();
        }
    }
}

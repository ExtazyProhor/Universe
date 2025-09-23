package ru.prohor.universe.jocasta.core.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileSystemUtils {
    private FileSystemUtils() {}

    public static UserDir userDir() {
        return new UserDir(System.getProperty("user.dir"));
    }

    public static class UserDir {
        private final String path;

        public UserDir(String path) {
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
    }
}

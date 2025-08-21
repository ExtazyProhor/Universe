package ru.prohor.universe.jocasta.core.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileSystemUtils {
    private FileSystemUtils() {}

    public static String userDir() {
        return System.getProperty("user.dir");
    }

    public static Path userDirPath() {
        return Paths.get(userDir());
    }

    public static File userDirFile() {
        return new File(userDir());
    }
}

package ru.prohor.universe.jocasta.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class SystemUtils {
    private SystemUtils() {}

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

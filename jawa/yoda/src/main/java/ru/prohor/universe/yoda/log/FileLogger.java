package ru.prohor.universe.yoda.log;

import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;
import ru.prohor.universe.jocasta.core.utils.DateTimeUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

public class FileLogger {
    private final Path path;

    public FileLogger(String logFilePath) {
        this.path = Path.of(logFilePath);
    }

    public void log(LogLevel logLevel, String message) {
        String lvl = logLevel.toString();
        Sneaky.silent(() -> Files.writeString(
                path, time() + " " + lvl + " ".repeat(6 - lvl.length()) + message, StandardOpenOption.APPEND
        ));
    }

    public void log(LogLevel logLevel, String message, Exception e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        log(logLevel, message + System.lineSeparator() + e(e));
    }

    public void log(LogLevel logLevel, Exception e) {
        log(logLevel, e(e));
    }

    private static String e(Exception e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return e.getClass().getCanonicalName() + ": " + e.getMessage() + System.lineSeparator() + writer;
    }

    private static String time() {
        return DateTimeUtil.toReadableString(Instant.now());
    }
}

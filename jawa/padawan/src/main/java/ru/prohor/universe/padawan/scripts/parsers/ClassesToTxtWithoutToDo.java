package ru.prohor.universe.padawan.scripts.parsers;

import jakarta.annotation.Nonnull;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassesToTxtWithoutToDo {
    public static void main(String[] args) throws Exception {
        Map<String, Path> files = collectJavaFiles();
        Files.write(
                Paths.get(".txt"), Files.readAllLines(Paths.get("list"))
                        .stream()
                        .flatMap(fileName -> sub(read(files.get(fileName))).stream())
                        .map(s -> s.contains("//") ? s.substring(0, s.indexOf("//")) + "// log" : s)
                        .toList()
        );
    }

    static List<String> sub(List<String> list) {
        int i = 0;
        for (String s : list)
            if (s.startsWith("public") || s.startsWith("@"))
                break;
            else
                i++;
        return list.subList(i, list.size());
    }

    static List<String> read(Path path) {
        try {
            return Files.readAllLines(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static Map<String, Path> collectJavaFiles() throws IOException {
        Map<String, Path> filesMap = new HashMap<>();
        Files.walkFileTree(
                Paths.get("src/main/java/ru/prohor/universe/scarif"), new SimpleFileVisitor<>() {
                    @Override
                    @Nonnull
                    public FileVisitResult visitFile(@Nonnull Path file, @Nonnull BasicFileAttributes attrs) {
                        if (file.toString().endsWith(".java")) {
                            String fileName = file.getFileName().toString();
                            String key = fileName.substring(0, fileName.length() - 5);
                            filesMap.put(key, file);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                }
        );
        return filesMap;
    }
}

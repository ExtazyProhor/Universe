package ru.prohor.universe.padawan.scripts.parsers;

import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;
import ru.prohor.universe.padawan.Padawan;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AllClassesToOneFile {
    public static void main(String[] args) throws Exception {
        mergeJavaFiles();
        smartMergeJavaFiles();
    }

    private static void mergeJavaFiles() {
        mergeFiles(
                Path.of("."),
                Padawan.resolve("txt.txt"),
                path -> path.toString().endsWith(".java")
        );
    }

    private static void smartMergeJavaFiles() {
        smartMergeFiles(
                new File("."),
                Padawan.resolve("txt.txt")
        );
    }

    private static void smartMergeFiles(File sourceDir, Path output) {
        Sneaky.execute(() -> {
            StringWriter stringWriter = new StringWriter();
            BufferedWriter writer = new BufferedWriter(stringWriter);
            Set<String> imports = new HashSet<>();
            smartProcessFile(sourceDir, writer, imports);
            Files.writeString(
                    output,
                    imports.stream()
                            .sorted()
                            .collect(Collectors.joining("\n")) + "\n\npublic class Wrapper {\n" + stringWriter + "}"
            );
        });
    }

    private static void smartProcessFile(File file, BufferedWriter writer, Set<String> imports) throws IOException {
        if (file.isDirectory()) {
            for (File subFile : Objects.requireNonNull(file.listFiles()))
                smartProcessFile(subFile, writer, imports);
            return;
        }
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String buffer = "";
        while (!buffer.startsWith("public") && !buffer.startsWith("@")) {
            buffer = reader.readLine();
            if (buffer.startsWith("import"))
                imports.add(buffer);
        }
        while (buffer.startsWith("@")) {
            writer.write("\t" + buffer);
            writer.newLine();
            buffer = reader.readLine();
        }
        writer.write("\tpublic static");
        writer.write(buffer.substring(6));
        writer.newLine();
        while ((buffer = reader.readLine()) != null) {
            writer.write("\t" + buffer);
            writer.newLine();
        }
        writer.newLine();
    }

    private static void mergeFiles(Path rootDir, Path outputFile, Predicate<Path> filter) {
        Sneaky.execute(() -> {
            try (Stream<Path> paths = Files.walk(rootDir)) {
                Files.deleteIfExists(outputFile);
                paths.filter(Files::isRegularFile)
                        .filter(filter)
                        .forEach(path -> Sneaky.execute(() -> {
                            Files.writeString(
                                    outputFile,
                                    Files.readString(path, StandardCharsets.UTF_8),
                                    StandardCharsets.UTF_8,
                                    StandardOpenOption.APPEND
                            );
                        }));
            }
        });
    }
}

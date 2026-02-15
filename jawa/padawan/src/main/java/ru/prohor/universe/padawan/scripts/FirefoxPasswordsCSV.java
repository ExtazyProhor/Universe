package ru.prohor.universe.padawan.scripts;

import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FirefoxPasswordsCSV {
    private static final String HEADER = Stream.of(
            "url",
            "username",
            "password",
            "httpRealm",
            "formActionOrigin",
            "guid",
            "timeCreated",
            "timeLastUsed",
            "timePasswordChanged"
    ).collect(Collectors.joining("\",\"", "\"", "\""));

    public static void main(String[] args) throws Exception {
        Stream.of("file1", "file2", "file3")
                .map(s -> Path.of("path/to/dir/" + s + ".csv"))
                .flatMap(path -> readCsvFile(path).stream())
                .map(FirefoxPasswordsCSV::parseString)
                .map(List::toString)
                .sorted()
                .forEach(System.out::println);
    }

    private static List<String> readCsvFile(Path path) {
        List<String> list = Sneaky.execute(() -> Files.readAllLines(path));
        if (list.isEmpty() || !list.getFirst().equals(HEADER))
            throw new RuntimeException("Illegal header or empty file");
        list = list.stream().filter(s -> !s.isBlank()).collect(Collectors.toCollection(ArrayList::new));
        list.remove(0);
        return list;
    }

    private static List<String> parseString(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        boolean wordParsing = false;
        for (char ch : line.toCharArray()) {
            if (wordParsing) {
                if (ch == '"')
                    wordParsing = false;
                else
                    builder.append(ch);
                continue;
            }
            switch (ch) {
                case ',' -> {
                    result.add(builder.toString());
                    builder.setLength(0);
                }
                case '"' -> wordParsing = true;
                default -> throw new RuntimeException("Illegal character '" + ch + "' outside the element");
            }
        }

        result.add(builder.toString());
        if (result.size() != 9)
            throw new RuntimeException("Illegal line's elements count: " + result.size());
        return result;
    }

    private record Password(
            String url,
            String username,
            String password,
            String httpRealm,
            String formActionOrigin,
            String guid,
            String timeCreated,
            String timeLastUsed,
            String timePasswordChanged
    ) {}
}

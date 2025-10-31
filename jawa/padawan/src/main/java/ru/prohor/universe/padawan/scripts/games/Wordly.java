package ru.prohor.universe.padawan.scripts.games;

import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;
import ru.prohor.universe.padawan.Padawan;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Wordly {
    private static final String DICTIONARY_PATH = Padawan.resolve("txt.txt").toString();
    private static final Charset CHARSET = StandardCharsets.UTF_8; // Charset.forName("Cp1251");

    public static void main(String[] args) {
        String mask = "вр***";
        String present = "";
        String absent = "";
        getAllWords(mask, present, absent).forEach(System.out::println);
    }

    private static List<String> getAllWords(String mask, String presentChars, String absentChars) {
        List<Character> present = presentChars.chars().mapToObj(c -> (char) c).toList();
        Set<Character> absent = absentChars.chars().mapToObj(c -> (char) c).collect(Collectors.toSet());

        List<String> words = new ArrayList<>();
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(DICTIONARY_PATH), CHARSET)
                )
        ) {
            String word;
            mainLoop:
            while ((word = reader.readLine()) != null) {
                if (mask.length() != word.length())
                    continue;
                for (int i = 0; i < word.length(); ++i) {
                    if (absent.contains(word.charAt(i)))
                        continue mainLoop;
                    if (mask.charAt(i) != '*')
                        if (word.charAt(i) != mask.charAt(i))
                            continue mainLoop;
                }
                for (char presentChar : present)
                    if (word.indexOf(presentChar) == -1)
                        continue mainLoop;
                words.add(word);
            }
        } catch (IOException e) {
            Sneaky.throwUnchecked(e);
        }
        return words;
    }
}

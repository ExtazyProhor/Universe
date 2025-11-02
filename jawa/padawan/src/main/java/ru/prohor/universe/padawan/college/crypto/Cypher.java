package ru.prohor.universe.padawan.college.crypto;

import ru.prohor.universe.jocasta.core.functional.DiFunction;

public enum Cypher {
    PERMUTATION(
            Permutation::encode,
            Permutation::decode
    ),
    COLUMN_PERMUTATION(
            ColumnPermutation::encode,
            ColumnPermutation::decode
    ),
    GAMMA(
            Gamma::encode,
            Gamma::decode
    ),
    VIGENERE(
            Vigenere::encode,
            Vigenere::decode
    );

    public String encode(String message, String key) {
        return encoder.apply(message, key);
    }

    public String decode(String encodedMessage, String key) {
        return decoder.apply(encodedMessage, key);
    }

    private final DiFunction<String, String, String> encoder;
    private final DiFunction<String, String, String> decoder;

    Cypher(DiFunction<String, String, String> encoder, DiFunction<String, String, String> decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }
}

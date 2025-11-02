package ru.prohor.universe.padawan.college.crypto;

import java.util.ArrayList;
import java.util.Collections;

public class CypherUtils {
    public static int getCharCode(char ch) {
        if (ch == ' ')
            return 33;
        if (ch <= 'е' && ch >= 'а')
            return ch - 'а';
        if (ch == 'ё')
            return 6;
        if (ch >= 'ж' && ch <= 'я')
            return ch - 'а' + 1;
        throw new IllegalArgumentException("Illegal character - '" + ch + "'");
    }

    public static char getChar(int code) {
        if (code == 33)
            return ' ';
        if (code >= 0 && code <= 5)
            return (char) (code + 'а');
        if (code == 6)
            return 'ё';
        if (code >= 7 && code <= 32)
            return (char) (code + 'а' - 1);
        throw new IllegalArgumentException("Illegal code - " + code);
    }

    public static int[] getCodesFromKey(String key) {
        int[] codes = new int[key.length()];
        ArrayList<KeyChar> toSort = new ArrayList<>(key.length());
        for (int i = 0; i < codes.length; ++i) {
            toSort.add(new KeyChar(getCharCode(key.charAt(i)) * key.length() + i, i));
        }
        Collections.sort(toSort);
        for (int i = 0; i < codes.length; ++i) {
            codes[i] = toSort.get(i).position;
        }
        return codes;
    }

    public static String extendMessageWithSpaces(String message, int keyLength) {
        int toAppend = message.length() % keyLength;
        return message.toLowerCase() + " ".repeat((toAppend == 0 ? 0 : (keyLength - toAppend)));
    }

    private record KeyChar(int sortIndex, int position) implements Comparable<KeyChar> {
        @Override
        public int compareTo(KeyChar o) {
            return Integer.compare(this.sortIndex, o.sortIndex);
        }
    }
}

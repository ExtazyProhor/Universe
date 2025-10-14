package ru.prohor.universe.jocasta.core.string;

public class OrdinalNumbers {
    private OrdinalNumbers() {}

    public static String of(int number) {
        if (number <= 0)
            throw new IllegalArgumentException(
                    "Ordinal numbers are only defined for positive integers: " + number + " is illegal"
            );
        return number + getSuffix(number);
    }

    private static String getSuffix(int number) {
        int mod100 = number % 100;
        if (mod100 >= 11 && mod100 <= 13)
            return "th";
        return switch (number % 10) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
    }
}

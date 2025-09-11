package ru.prohor.universe.jocasta.core.string;

public class OrdinalNumbers {
    private OrdinalNumbers() {}

    public static String of(int i) {
        return switch (i) {
            case 1 -> "First";
            case 2 -> "Second";
            case 3 -> "Third";
            case 4 -> "Fourth";
            case 5 -> "Fifth";
            case 6 -> "Sixth";
            case 7 -> "Seventh";
            case 8 -> "Eighth";
            case 9 -> "Ninth";
            case 10 -> "Tenth";
            default -> throw new IllegalArgumentException("numbers before 1 and after 10 are not supported");
        };
    }
}

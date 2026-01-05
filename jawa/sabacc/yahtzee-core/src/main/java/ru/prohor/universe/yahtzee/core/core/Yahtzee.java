package ru.prohor.universe.yahtzee.core.core;

public class Yahtzee {
    public static final int BONUS_VALUE = 35;
    public static final int BONUS_CONDITION = 63;
    public static final int COMBINATIONS = 15;

    public static boolean isSimple(Combination combination) {
        return switch (combination) {
            case UNITS, TWOS, THREES, FOURS, FIVES, SIXES -> true;
            default -> false;
        };
    }

    public static boolean isVariable(Combination combination) {
        return switch (combination) {
            case PAIR, TWO_PAIRS, THREE_OF_KIND, FOUR_OF_KIND, CHANCE -> true;
            default -> false;
        };
    }

    public static int getSimpleCombinationDenomination(Combination combination) {
        return switch (combination) {
            case UNITS -> 1;
            case TWOS -> 2;
            case THREES -> 3;
            case FOURS -> 4;
            case FIVES -> 5;
            case SIXES -> 6;
            default -> throw new IllegalArgumentException(combination + " is not simple");
        };
    }

    public static boolean isValidCombinationValue(Combination combination, int value) {
        return switch (combination) {
            case UNITS -> simple(value, 1);
            case TWOS -> simple(value, 2);
            case THREES -> simple(value, 3);
            case FOURS -> simple(value, 4);
            case FIVES -> simple(value, 5);
            case SIXES -> simple(value, 6);

            case PAIR, TWO_PAIRS, THREE_OF_KIND, FOUR_OF_KIND -> inRange(value, 5, 30) || value == 0;
            case FULL_HOUSE -> zeroOr(value, 25);
            case LOW_STRAIGHT -> zeroOr(value, 30);
            case HIGH_STRAIGHT -> zeroOr(value, 40);
            case YAHTZEE -> zeroOr(value, 50);
            case CHANCE -> inRange(value, 5, 30);
        };
    }

    private static boolean simple(int value, int digit) {
        return inRange(value, 0, digit * 5) && value % digit == 0;
    }

    private static boolean zeroOr(int value, int target) {
        return value == 0 || value == target;
    }

    private static boolean inRange(int value, int min, int max) {
        return value >= min && value <= max;
    }
}

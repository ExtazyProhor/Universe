package ru.prohor.universe.jocasta.core.string;

public class AnsiColors {
    private AnsiColors() {}

    private static final String PREFIX = "\u001B[";
    private static final String POSTFIX = "m";
    private static final String RESET = PREFIX + "0" + POSTFIX;

    public static String code(Colors textColor, Colors backgroundColor) {
        return code(textColor) + PREFIX +
                (backgroundColor.code / 10 * 50 + 40 + backgroundColor.code) + POSTFIX;
    }

    public static String code(Colors textColor) {
        return PREFIX + (textColor.code / 10 * 50 + 30 + textColor.code) + POSTFIX;
    }

    public static String reset() {
        return RESET;
    }

    public enum Colors {
        BLACK(0),
        RED(1),
        GREEN(2),
        YELLOW(3),
        BLUE(4),
        PURPLE(5),
        CYAN(6),
        LIGHT_GREY(7),

        GREY(10),
        LIGHT_RED(11),
        LIGHT_GREEN(12),
        LIGHT_YELLOW(13),
        LIGHT_BLUE(14),
        LIGHT_PURPLE(15),
        LIGHT_CYAN(16),
        WHITE(17);

        private final int code;

        Colors(int code) {
            this.code = code;
        }
    }
}

package ru.prohor.universe.jocasta.core.utils;

import jakarta.annotation.Nonnull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtil {
    static {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    private static final Locale RUSSIAN_LOCALE = Locale.forLanguageTag("ru-RU");
    public static final ZoneId MOSCOW_ZONE_ID = ZoneId.of("Europe/Moscow");
    private static final DateTimeFormatter JAVA_DIGIT_FORMATTER = DateTimeFormatter
            .ofPattern("dd.MM.yyyy, HH:mm:ss")
            .withZone(MOSCOW_ZONE_ID);
    private static final DateTimeFormatter JAVA_DIGIT_FORMATTER2 = DateTimeFormatter
            .ofPattern("HH:mm:ss dd-MM-yyyy")
            .withZone(MOSCOW_ZONE_ID);
    private static final DateTimeFormatter JAVA_RUSSIAN_FULL_FORMATTER =
            DateTimeFormatter.ofPattern("d MMMM yyyy", RUSSIAN_LOCALE);
    private static final DateTimeFormatter JAVA_RUSSIAN_WITHOUT_YEAR_FORMATTER =
            DateTimeFormatter.ofPattern("d MMMM", RUSSIAN_LOCALE);
    private static final DateTimeFormatter JAVA_LOCAL_TIME_WITHOUT_MILLIS = DateTimeFormatter
            .ofPattern("HH:mm:ss");

    @Nonnull
    public static String toReadableString(@Nonnull Instant instant) {
        return JAVA_DIGIT_FORMATTER.format(instant);
    }

    /**
     * for example, <code>15:33:42 16-12-2025</code>
     */
    @Nonnull
    public static String toDigitsString(@Nonnull Instant instant) {
        return JAVA_DIGIT_FORMATTER2.format(instant);
    }

    @Nonnull
    public static String russianMonth(int month) {
        return Month.of(month).getDisplayName(TextStyle.FULL, RUSSIAN_LOCALE);
    }

    /**
     * for example, <code>23 декабря 2025</code>
     */
    @Nonnull
    public static String russianFullDate(LocalDate date) {
        return JAVA_RUSSIAN_FULL_FORMATTER.format(date);
    }

    /**
     * for example, <code>23 декабря</code>
     */
    @Nonnull
    public static String russianDateWithoutYear(LocalDate date) {
        return JAVA_RUSSIAN_WITHOUT_YEAR_FORMATTER.format(date);
    }

    /**
     * for example, <code>12:15:34</code>
     */
    @Nonnull
    public static String formatWithoutMillis(LocalTime time) {
        return JAVA_LOCAL_TIME_WITHOUT_MILLIS.format(time);
    }
}

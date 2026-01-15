package ru.prohor.universe.jocasta.core.utils;

import jakarta.annotation.Nonnull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTimeUtil {
    private static final Locale RUS = Locale.forLanguageTag("ru-RU");
    public static final ZoneId MOSCOW_ZONE_ID = ZoneId.of("Europe/Moscow");

    private static final DateTimeFormatter DATE_TIME_DIGIT = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss")
            .withZone(MOSCOW_ZONE_ID);
    private static final DateTimeFormatter DATE_TEXT = DateTimeFormatter.ofPattern("d MMMM yyyy", RUS);
    private static final DateTimeFormatter DATE_WITHOUT_YEAR_TEXT = DateTimeFormatter.ofPattern("d MMMM", RUS);
    private static final DateTimeFormatter TIME_WITHOUT_MILLIS = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * for example, <code>16-12-2025. 15:33:42</code>
     */
    @Nonnull
    public static String toReadableString(@Nonnull Instant instant) {
        return DATE_TIME_DIGIT.format(instant);
    }

    /**
     * for example, <code>23 декабря 2025</code>
     */
    @Nonnull
    public static String dateText(LocalDate date) {
        return DATE_TEXT.format(date);
    }

    /**
     * for example, <code>23 декабря</code>
     */
    @Nonnull
    public static String dateWithoutYearText(LocalDate date) {
        return DATE_WITHOUT_YEAR_TEXT.format(date);
    }

    /**
     * for example, <code>12:15:34</code>
     */
    @Nonnull
    public static String timeWithoutMillis(LocalTime time) {
        return TIME_WITHOUT_MILLIS.format(time);
    }
}

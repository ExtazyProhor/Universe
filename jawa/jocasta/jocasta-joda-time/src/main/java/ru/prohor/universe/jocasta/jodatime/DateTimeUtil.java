package ru.prohor.universe.jocasta.jodatime;

import jakarta.annotation.Nonnull;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public class DateTimeUtil {
    private static final int NANOS_IN_MILLI = 1_000_000;
    private static final Locale RUSSIAN_LOCALE = Locale.forLanguageTag("ru-RU");
    private static final DateTimeZone MOSCOW_ZONE = DateTimeZone.forID("Europe/Moscow");
    private static final DateTimeFormatter BASE_FORMATTER = DateTimeFormat
            .forStyle("SM")
            .withZone(MOSCOW_ZONE);
    private static final DateTimeFormatter DIGIT_FORMATTER = DateTimeFormat
            .forPattern("HH:mm:ss dd-MM-yyyy")
            .withZone(MOSCOW_ZONE);

    /**
     * Instant
     */
    @Nonnull
    public static java.time.Instant unwrap(@Nonnull Instant instant) {
        return java.time.Instant.ofEpochMilli(instant.getMillis());
    }

    @Nonnull
    public static Instant wrap(@Nonnull java.time.Instant instant) {
        return Instant.ofEpochMilli(instant.toEpochMilli());
    }

    /**
     * LocalDate
     */
    @Nonnull
    public static LocalDate wrap(@Nonnull java.time.LocalDate date) {
        return new LocalDate(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    @Nonnull
    public static java.time.LocalDate unwrap(@Nonnull LocalDate date) {
        return java.time.LocalDate.of(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
    }

    /**
     * LocalTime
     */
    @Nonnull
    public static LocalTime wrap(@Nonnull java.time.LocalTime time) {
        return new LocalTime(
                time.getHour(),
                time.getMinute(),
                time.getSecond(),
                time.getNano() / NANOS_IN_MILLI
        );
    }

    @Nonnull
    public static java.time.LocalTime unwrap(@Nonnull LocalTime time) {
        return java.time.LocalTime.of(
                time.getHourOfDay(),
                time.getMinuteOfHour(),
                time.getSecondOfMinute(),
                time.getMillisOfSecond() * NANOS_IN_MILLI
        );
    }

    /**
     * for example, <code>16.12.2025, 15:33:42</code>
     */
    @Nonnull
    public static String toReadableString(@Nonnull Instant instant) {
        return instant.toString(BASE_FORMATTER);
    }


    /**
     * for example, <code>15:33:42 16-12-2025</code>
     */
    @Nonnull
    public static String toDigitsString(@Nonnull Instant instant) {
        return instant.toString(DIGIT_FORMATTER);
    }

    @Nonnull
    public static String russianMonth(int month) {
        return Month.of(month).getDisplayName(TextStyle.FULL, RUSSIAN_LOCALE);
    }

    public static DateTimeZone zoneMoscow() {
        return MOSCOW_ZONE;
    }
}

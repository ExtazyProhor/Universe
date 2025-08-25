package ru.prohor.universe.jocasta.jodaTime;

import jakarta.annotation.Nonnull;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final int NANOS_IN_MILLI = 1_000_000;
    private static final DateTimeFormatter BASE_FORMATTER = DateTimeFormat
            .forStyle("SM")
            .withZone(DateTimeZone.forID("Europe/Moscow"));

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

    @Nonnull
    public static String toReadableString(@Nonnull Instant instant) {
        return instant.toString(BASE_FORMATTER);
    }
}

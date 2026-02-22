package ru.prohor.universe.jocasta.core.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DateTimeUtilTest {

    @Test
    void toReadableString_shouldFormatInstantCorrectly() {
        Instant instant = ZonedDateTime.of(2025, 12, 16, 15, 33, 42, 0, DateTimeUtil.MOSCOW_ZONE_ID).toInstant();
        String result = DateTimeUtil.toReadableString(instant);
        assertEquals("16.12.2025, 15:33:42", result);
    }

    @Test
    void toReadableString_shouldHandleMidnight() {
        Instant instant = ZonedDateTime.of(2025, 1, 1, 0, 0, 0, 0, DateTimeUtil.MOSCOW_ZONE_ID).toInstant();
        String result = DateTimeUtil.toReadableString(instant);
        assertEquals("01.01.2025, 00:00:00", result);
    }

    @Test
    void toReadableString_shouldHandleEndOfDay() {
        Instant instant = ZonedDateTime.of(2025, 12, 31, 23, 59, 59, 0, DateTimeUtil.MOSCOW_ZONE_ID).toInstant();
        String result = DateTimeUtil.toReadableString(instant);
        assertEquals("31.12.2025, 23:59:59", result);
    }

    @ParameterizedTest
    @MethodSource("provideDatesForDateText")
    void dateText_shouldFormatDateCorrectly(LocalDate date, String expected) {
        String result = DateTimeUtil.dateText(date);
        assertEquals(expected, result);
    }

    private static Stream<Arguments> provideDatesForDateText() {
        return Stream.of(
                Arguments.of(LocalDate.of(2025, 12, 23), "23 декабря 2025"),
                Arguments.of(LocalDate.of(2025, 1, 1), "1 января 2025"),
                Arguments.of(LocalDate.of(2025, 2, 14), "14 февраля 2025"),
                Arguments.of(LocalDate.of(2025, 3, 8), "8 марта 2025"),
                Arguments.of(LocalDate.of(2025, 5, 9), "9 мая 2025"),
                Arguments.of(LocalDate.of(2025, 6, 30), "30 июня 2025"),
                Arguments.of(LocalDate.of(2025, 7, 15), "15 июля 2025"),
                Arguments.of(LocalDate.of(2025, 8, 20), "20 августа 2025"),
                Arguments.of(LocalDate.of(2025, 9, 1), "1 сентября 2025"),
                Arguments.of(LocalDate.of(2025, 10, 31), "31 октября 2025"),
                Arguments.of(LocalDate.of(2025, 11, 11), "11 ноября 2025"),
                Arguments.of(LocalDate.of(2000, 2, 29), "29 февраля 2000") // leap year
        );
    }

    @ParameterizedTest
    @MethodSource("provideDatesForDateWithoutYearText")
    void dateWithoutYearText_shouldFormatDateCorrectly(LocalDate date, String expected) {
        String result = DateTimeUtil.dateWithoutYearText(date);
        assertEquals(expected, result);
    }

    private static Stream<Arguments> provideDatesForDateWithoutYearText() {
        return Stream.of(
                Arguments.of(LocalDate.of(2025, 12, 23), "23 декабря"),
                Arguments.of(LocalDate.of(2025, 1, 1), "1 января"),
                Arguments.of(LocalDate.of(2025, 2, 14), "14 февраля"),
                Arguments.of(LocalDate.of(2025, 3, 8), "8 марта"),
                Arguments.of(LocalDate.of(2025, 5, 9), "9 мая"),
                Arguments.of(LocalDate.of(2025, 6, 30), "30 июня"),
                Arguments.of(LocalDate.of(2025, 7, 15), "15 июля"),
                Arguments.of(LocalDate.of(2025, 8, 20), "20 августа"),
                Arguments.of(LocalDate.of(2025, 9, 1), "1 сентября"),
                Arguments.of(LocalDate.of(2025, 10, 31), "31 октября"),
                Arguments.of(LocalDate.of(2025, 11, 11), "11 ноября")
        );
    }

    @ParameterizedTest
    @MethodSource("provideTimesForTimeWithoutMillis")
    void timeWithoutMillis_shouldFormatTimeCorrectly(LocalTime time, String expected) {
        String result = DateTimeUtil.timeWithoutMillis(time);
        assertEquals(expected, result);
    }

    private static Stream<Arguments> provideTimesForTimeWithoutMillis() {
        return Stream.of(
                Arguments.of(LocalTime.of(12, 15, 34), "12:15:34"),
                Arguments.of(LocalTime.of(0, 0, 0), "00:00:00"),
                Arguments.of(LocalTime.of(23, 59, 59), "23:59:59"),
                Arguments.of(LocalTime.of(9, 5, 7), "09:05:07"),
                Arguments.of(LocalTime.of(15, 30, 0), "15:30:00"),
                Arguments.of(LocalTime.of(12, 15, 34, 499_999_999), "12:15:34") // with nanoseconds
        );
    }

    @Test
    void timeWithoutMillis_shouldIgnoreMilliseconds() {
        LocalTime timeWithMillis = LocalTime.of(14, 25, 36, 123_456_789);
        String result = DateTimeUtil.timeWithoutMillis(timeWithMillis);
        assertEquals("14:25:36", result);
        assertFalse(result.contains("123"));
    }

    @Test
    void moscowZoneId_shouldBeCorrect() {
        assertEquals("Europe/Moscow", DateTimeUtil.MOSCOW_ZONE_ID.getId());
    }

    @Test
    void toReadableString_shouldHandleLeapYear() {
        Instant instant = ZonedDateTime.of(2024, 2, 29, 12, 0, 0, 0, DateTimeUtil.MOSCOW_ZONE_ID).toInstant();

        String result = DateTimeUtil.toReadableString(instant);

        assertEquals("29.02.2024, 12:00:00", result);
    }

    @Test
    void toReadableString_shouldHandleSingleDigitDayAndMonth() {
        Instant instant = ZonedDateTime.of(2025, 1, 5, 9, 8, 7, 0, DateTimeUtil.MOSCOW_ZONE_ID).toInstant();
        String result = DateTimeUtil.toReadableString(instant);
        assertEquals("05.01.2025, 09:08:07", result);
    }

    @Test
    void dateText_shouldHandleDifferentYears() {
        LocalDate pastDate = LocalDate.of(1999, 12, 31);
        LocalDate futureDate = LocalDate.of(2099, 1, 1);

        String pastResult = DateTimeUtil.dateText(pastDate);
        String futureResult = DateTimeUtil.dateText(futureDate);

        assertEquals("31 декабря 1999", pastResult);
        assertEquals("1 января 2099", futureResult);
    }
}

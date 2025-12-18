package ru.prohor.universe.bobafett.feature.holidays;

import org.joda.time.LocalDate;

import java.util.List;

public interface RegularHolidaysProvider {
    List<String> getForDate(int currentYear, LocalDate date);
}

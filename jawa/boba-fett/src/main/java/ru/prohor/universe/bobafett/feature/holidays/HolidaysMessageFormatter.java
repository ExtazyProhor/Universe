package ru.prohor.universe.bobafett.feature.holidays;

import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.time.LocalDate;
import java.util.List;

public interface HolidaysMessageFormatter {
    String format(LocalDate date, Opt<List<String>> customHolidays, List<String> holidays);

    String formatReminder(List<String> holidays);
}

package ru.prohor.universe.bobafett.feature.holidays;

import org.joda.time.LocalDate;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.util.List;

public interface HolidaysMessageFormatter {
    String format(LocalDate date, Opt<List<String>> customHolidays, List<String> holidays);

    String formatReminder(List<String> holidays);
}

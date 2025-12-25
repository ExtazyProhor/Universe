package ru.prohor.universe.bobafett.feature.holidays;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.jodatime.DateTimeUtil;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HolidaysMessageFormatterImpl implements HolidaysMessageFormatter {
    @Override
    public String format(LocalDate date, Opt<List<String>> customHolidays, List<String> holidays) {
        String dateHeader = date.getDayOfMonth() + " " + DateTimeUtil.russianMonth(date.getMonthOfYear());
        Opt<String> customHolidaysPart = customHolidays.map(this::formatHolidaysList);
        String holidaysPart = formatHolidaysList(holidays);

        return dateHeader + "\n\n" + customHolidaysPart.map(it -> it + "\n\n").orElse("") + holidaysPart;
    }

    @Override
    public String formatReminder(List<String> holidays) {
        return "Напоминаю, что через неделю будет:\n\n" + formatHolidaysList(holidays);
    }

    private String formatHolidaysList(List<String> holidays) {
        return holidays.stream().map(x -> "– " + x).collect(Collectors.joining("\n"));
    }
}

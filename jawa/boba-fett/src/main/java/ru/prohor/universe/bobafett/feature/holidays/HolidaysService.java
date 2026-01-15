package ru.prohor.universe.bobafett.feature.holidays;

import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.data.pojo.CustomHoliday;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.time.LocalDate;
import java.util.List;

@Service
public class HolidaysService {
    private final HolidaysMessageFormatter holidaysMessageFormatter;
    private final RegularHolidaysProvider regularHolidaysProvider;

    public HolidaysService(
            HolidaysMessageFormatter holidaysMessageFormatter,
            RegularHolidaysProvider regularHolidaysProvider
    ) {
        this.holidaysMessageFormatter = holidaysMessageFormatter;
        this.regularHolidaysProvider = regularHolidaysProvider;
    }

    public String getHolidaysMessageForDate(LocalDate date, int year, Opt<List<String>> customHolidays) {
        return holidaysMessageFormatter.format(
                date,
                customHolidays,
                regularHolidaysProvider.getForDate(
                        year,
                        date
                )
        );
    }

    public String getHolidaysMessageForDate(LocalDate date, int year, List<CustomHoliday> customHolidays) {
        List<String> names = customHolidays.stream().map(CustomHoliday::holidayName).toList();
        return getHolidaysMessageForDate(date, year, Opt.when(!customHolidays.isEmpty(), names));
    }
}

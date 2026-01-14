package ru.prohor.universe.bobafett.feature.holidays;

import org.joda.time.LocalDate;
import ru.prohor.universe.bobafett.data.pojo.CustomHoliday;
import ru.prohor.universe.jocasta.jodatime.DateTimeUtil;

public record DistributionDays(
        LocalDate today,
        LocalDate tomorrow,
        LocalDate dayAfterTomorrow
) {
    public static DistributionDays create() {
        LocalDate today = LocalDate.now(DateTimeUtil.zoneMoscow());
        return new DistributionDays(
                today,
                today.plusDays(1),
                today.plusDays(2)
        );
    }

    public LocalDate getLocalDate(int indent) {
        return switch (indent) {
            case 0 -> today;
            case 1 -> tomorrow;
            case 2 -> dayAfterTomorrow;
            default -> throw new IllegalArgumentException("Illegal indent: " + indent);
        };
    }

    public int getIndent(CustomHoliday holiday) {
        int day = holiday.dayOfMonth();
        int month = holiday.month();
        if (compare(today, day, month))
            return 0;
        if (compare(tomorrow, day, month))
            return 1;
        if (compare(dayAfterTomorrow, day, month))
            return 2;
        throw new IllegalArgumentException("Illegal holiday date: day=" + day + ", month=" + month);
    }

    private boolean compare(LocalDate date, int day, int month) {
        return date.getDayOfMonth() == day && date.getMonthOfYear() == month;
    }
}

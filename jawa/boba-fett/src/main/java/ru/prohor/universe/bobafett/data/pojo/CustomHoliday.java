package ru.prohor.universe.bobafett.data.pojo;

import org.bson.types.ObjectId;
import ru.prohor.universe.bobafett.data.dto.CustomHolidayDto;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;

public record CustomHoliday(
        ObjectId id,
        long chatId,
        int month,
        int dayOfMonth,
        String holidayName
) implements MongoEntityPojo<CustomHolidayDto> {
    @Override
    public CustomHolidayDto toDto() {
        return new CustomHolidayDto(
                id,
                chatId,
                month,
                dayOfMonth,
                holidayName
        );
    }

    public static CustomHoliday fromDto(CustomHolidayDto customHoliday) {
        return new CustomHoliday(
                customHoliday.getId(),
                customHoliday.getChatId(),
                customHoliday.getMonth(),
                customHoliday.getDayOfMonth(),
                customHoliday.getHolidayName()
        );
    }
}

package ru.prohor.universe.bobafett.data.dto;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity("custom_holidays")
public class CustomHolidayDto {
    @Id
    private ObjectId id;
    @Property("chat_id")
    private long chatId;
    private int month;
    @Property("day_of_month")
    private int dayOfMonth;
    @Property("holiday_name")
    private String holidayName;
}

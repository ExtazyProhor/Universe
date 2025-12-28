package ru.prohor.universe.jocasta.jackson.jodatime.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.MonthDay;

import java.io.IOException;

public class MonthDaySerializer extends JsonSerializer<MonthDay> {
    @Override
    public void serialize(MonthDay value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(value.getMonthOfYear() + "-" + value.getDayOfMonth());
    }
}

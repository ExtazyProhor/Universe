package ru.prohor.universe.jocasta.jackson.jodatime.deserializer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.time.MonthDay;

import java.io.IOException;

public class MonthDayDeserializer extends JsonDeserializer<MonthDay> {
    @Override
    public MonthDay deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String raw = parser.getValueAsString();
        String[] parts = raw.split("-");
        if (parts.length != 2)
            throw new JsonParseException(parser, "month-day string must contains exactly one dash");
        return new MonthDay(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }
}

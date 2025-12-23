package ru.prohor.universe.jocasta.jackson.jodatime.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.time.Instant;

import java.io.IOException;

public class InstantDeserializer extends JsonDeserializer<Instant> {
    @Override
    public Instant deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        long unixSeconds = parser.getValueAsLong();
        return new Instant(unixSeconds * 1000);
    }
}

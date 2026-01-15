package ru.prohor.universe.jocasta.jackson.jodatime.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;

/**
 * Read {@link Instant} as unix timestamp
 */
public class InstantDeserializer extends JsonDeserializer<Instant> {
    @Override
    public Instant deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return Instant.ofEpochSecond(parser.getValueAsInt());
    }
}

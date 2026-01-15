package ru.prohor.universe.jocasta.jackson.core.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ru.prohor.universe.jocasta.core.collections.common.Bool;

import java.io.IOException;

/**
 * Allows to save from 3 to 4 bytes (depending on the values - <code>true</code> or <code>false</code>)
 * when serializing to JSON
 */
public class BoolDeserializer extends JsonDeserializer<Bool> {
    @Override
    public Bool deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return Bool.of(parser.getValueAsInt() != 0);
    }
}

package ru.prohor.universe.jocasta.jackson.jodatime.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ru.prohor.universe.jocasta.core.collections.common.Bool;

import java.io.IOException;

public class BoolDeserializer extends JsonDeserializer<Bool> {
    @Override
    public Bool deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return new Bool(parser.getValueAsInt() != 0);
    }
}

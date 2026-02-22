package ru.prohor.universe.jocasta.jackson.core.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ru.prohor.universe.jocasta.core.collections.common.Bool;

import java.io.IOException;

/**
 * Allows to save from 3 to 4 bytes (depending on the values - <code>true</code> or <code>false</code>)
 * when serializing to JSON
 */
public class BoolSerializer extends JsonSerializer<Bool> {
    @Override
    public void serialize(Bool value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeNumber(value.asInt());
    }
}

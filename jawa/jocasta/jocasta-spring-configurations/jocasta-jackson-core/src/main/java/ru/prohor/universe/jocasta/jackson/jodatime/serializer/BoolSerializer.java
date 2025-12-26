package ru.prohor.universe.jocasta.jackson.jodatime.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ru.prohor.universe.jocasta.core.collections.common.Bool;

import java.io.IOException;

public class BoolSerializer extends JsonSerializer<Bool> {
    @Override
    public void serialize(Bool value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeNumber(value.value() ? 1 : 0);
    }
}

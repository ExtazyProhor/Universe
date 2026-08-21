package ru.prohor.universe.jocasta.core.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.BitSet;

public class BitSetSerializer extends JsonSerializer<BitSet> {
    @Override
    public void serialize(BitSet value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeBinary(value.toByteArray());
    }
}

package ru.prohor.universe.jocasta.jackson.morphia;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.bson.types.ObjectId;

import java.io.IOException;

public class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {
    @Override
    public ObjectId deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return new ObjectId(parser.getValueAsString());
    }
}

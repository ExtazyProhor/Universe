package ru.prohor.universe.jocasta.morphia.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import org.bson.types.ObjectId;

public class JacksonMorphiaModule extends Module {
    @Override
    public void setupModule(SetupContext context) {
        SimpleSerializers serializers = new SimpleSerializers();
        serializers.addSerializer(ObjectId.class, new ObjectIdSerializer());
        context.addSerializers(serializers);

        SimpleDeserializers deserializers = new SimpleDeserializers();
        deserializers.addDeserializer(ObjectId.class, new ObjectIdDeserializer());
        context.addDeserializers(deserializers);
    }

    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    @Override
    public String getModuleName() {
        return "JacksonMorphiaModule";
    }
}

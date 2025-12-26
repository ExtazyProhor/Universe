package ru.prohor.universe.jocasta.jackson.jodatime;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import ru.prohor.universe.jocasta.core.collections.common.Bool;
import ru.prohor.universe.jocasta.jackson.jodatime.deserializer.BoolDeserializer;
import ru.prohor.universe.jocasta.jackson.jodatime.deserializer.JocastaCoreDeserializers;
import ru.prohor.universe.jocasta.jackson.jodatime.serializer.BoolSerializer;
import ru.prohor.universe.jocasta.jackson.jodatime.serializer.JocastaCoreSerializers;

public class JocastaCoreModule extends Module {
    @Override
    public void setupModule(SetupContext context) {
        context.addSerializers(new JocastaCoreSerializers());
        context.addDeserializers(new JocastaCoreDeserializers());
        context.addTypeModifier(new JocastaCoreTypeModifier());

        SimpleSerializers serializers = new SimpleSerializers();
        serializers.addSerializer(Bool.class, new BoolSerializer());
        context.addSerializers(serializers);

        SimpleDeserializers deserializers = new SimpleDeserializers();
        deserializers.addDeserializer(Bool.class, new BoolDeserializer());
        context.addDeserializers(deserializers);
    }

    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    @Override
    public String getModuleName() {
        return "JocastaCoreModule";
    }
}

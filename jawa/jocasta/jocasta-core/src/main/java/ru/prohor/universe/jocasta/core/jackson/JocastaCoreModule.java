package ru.prohor.universe.jocasta.core.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import ru.prohor.universe.jocasta.core.collections.common.Bool;
import ru.prohor.universe.jocasta.core.jackson.deserializer.BoolDeserializer;
import ru.prohor.universe.jocasta.core.jackson.deserializer.InstantDeserializer;
import ru.prohor.universe.jocasta.core.jackson.deserializer.JocastaCoreDeserializers;
import ru.prohor.universe.jocasta.core.jackson.deserializer.LocalDateDeserializer;
import ru.prohor.universe.jocasta.core.jackson.serializer.BoolSerializer;
import ru.prohor.universe.jocasta.core.jackson.serializer.InstantSerializer;
import ru.prohor.universe.jocasta.core.jackson.serializer.JocastaCoreSerializers;
import ru.prohor.universe.jocasta.core.jackson.serializer.LocalDateSerializer;

import java.time.Instant;
import java.time.LocalDate;

public class JocastaCoreModule extends Module {
    @Override
    public void setupModule(SetupContext context) {
        context.addSerializers(new JocastaCoreSerializers());
        context.addDeserializers(new JocastaCoreDeserializers());
        context.addTypeModifier(new JocastaCoreTypeModifier());

        SimpleSerializers serializers = new SimpleSerializers();
        serializers.addSerializer(Bool.class, new BoolSerializer());
        serializers.addSerializer(Instant.class, new InstantSerializer());
        serializers.addSerializer(LocalDate.class, new LocalDateSerializer());
        context.addSerializers(serializers);

        SimpleDeserializers deserializers = new SimpleDeserializers();
        deserializers.addDeserializer(Bool.class, new BoolDeserializer());
        deserializers.addDeserializer(Instant.class, new InstantDeserializer());
        deserializers.addDeserializer(LocalDate.class, new LocalDateDeserializer());
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

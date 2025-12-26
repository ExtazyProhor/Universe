package ru.prohor.universe.jocasta.jackson.jodatime.jdk8;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

public class JocastaCoreModule extends Module {
    @Override
    public void setupModule(SetupContext context) {
        context.addSerializers(new JocastaCoreSerializers());
        context.addDeserializers(new JocastaCoreDeserializers());
        context.addTypeModifier(new JocastaCoreTypeModifier());
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

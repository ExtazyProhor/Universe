package ru.prohor.universe.scarif.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.coreConfigurations.SnowflakeConfiguration;
import ru.prohor.universe.jocasta.holocron.HolocronConfiguration;

@Configuration
@Import({
        SnowflakeConfiguration.class,
        HolocronConfiguration.class
})
public class JocastaConfiguration {}

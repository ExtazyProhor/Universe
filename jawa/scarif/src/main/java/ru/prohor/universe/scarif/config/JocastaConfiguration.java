package ru.prohor.universe.scarif.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.coreConfigurations.SnowflakeConfiguration;

@Configuration
@Import({
        SnowflakeConfiguration.class
})
public class JocastaConfiguration {}

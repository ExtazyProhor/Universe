package ru.prohor.universe.jocasta.coreConfigurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prohor.universe.jocasta.core.tools.SnowflakeIdGenerator;

@Configuration
public class SnowflakeConfiguration {
    @Bean
    public SnowflakeIdGenerator snowflakeIdGenerator(
            @Value("${universe.jocasta.core.snowflake.mainWorkerId}") int workerId
    ) {
        return new SnowflakeIdGenerator(workerId);
    }
}

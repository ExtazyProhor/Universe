package ru.prohor.universe.scarif;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.morphia.jackson.JacksonMorphiaConfiguration;
import ru.prohor.universe.jocasta.spring.configuration.HolocronConfiguration;
import ru.prohor.universe.jocasta.spring.configuration.JocastaAutoConfiguration;
import ru.prohor.universe.jocasta.spring.configuration.SnowflakeConfiguration;
import ru.prohor.universe.jocasta.springweb.configuration.GlobalExceptionControllerConfiguration;
import ru.prohor.universe.scarif.jwt.ScarifJwtConfiguration;

@Configuration
@ComponentScan
@Import({
        JocastaAutoConfiguration.class,
        SnowflakeConfiguration.class,
        HolocronConfiguration.class,
        ScarifJwtConfiguration.class,
        JacksonMorphiaConfiguration.class,
        GlobalExceptionControllerConfiguration.class,
})
public class ScarifMain {
    static void main(String[] args) {
        SpringApplication.run(ScarifMain.class, args);
    }
}

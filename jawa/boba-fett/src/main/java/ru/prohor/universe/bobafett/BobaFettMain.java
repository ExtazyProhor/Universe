package ru.prohor.universe.bobafett;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.prohor.universe.jocasta.jackson.core.JacksonJocastaCoreConfiguration;
import ru.prohor.universe.jocasta.jackson.jodatime.JacksonJodaTimeConfiguration;
import ru.prohor.universe.jocasta.jackson.morphia.JacksonMorphiaConfiguration;
import ru.prohor.universe.jocasta.spring.configuration.HolocronConfiguration;
import ru.prohor.universe.jocasta.spring.configuration.JocastaAutoConfiguration;

@Configuration
@ComponentScan
@EnableScheduling
@Import({
        JocastaAutoConfiguration.class,
        HolocronConfiguration.class,
        JacksonJodaTimeConfiguration.class,
        JacksonMorphiaConfiguration.class,
        JacksonJocastaCoreConfiguration.class,
})
public class BobaFettMain {
    public static void main(String[] args) {
        SpringApplication.run(BobaFettMain.class, args);
    }
}

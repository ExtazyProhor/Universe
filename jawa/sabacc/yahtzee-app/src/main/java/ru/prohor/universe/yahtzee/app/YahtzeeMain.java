package ru.prohor.universe.yahtzee.app;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.core.jackson.JacksonJocastaCoreConfiguration;
import ru.prohor.universe.jocasta.jackson.morphia.JacksonMorphiaConfiguration;
import ru.prohor.universe.jocasta.spring.configuration.HolocronConfiguration;
import ru.prohor.universe.jocasta.spring.configuration.JocastaAutoConfiguration;
import ru.prohor.universe.jocasta.springweb.configuration.AllControllersConfiguration;
import ru.prohor.universe.scarif.jwt.ScarifJwtConfiguration;
import ru.prohor.universe.yahtzee.core.YahtzeeCoreConfiguration;
import ru.prohor.universe.yahtzee.stats.YahtzeeStatisticsConfiguration;

@Configuration
@ComponentScan
@Import({
        JocastaAutoConfiguration.class,
        HolocronConfiguration.class,
        ScarifJwtConfiguration.class,
        JacksonMorphiaConfiguration.class,
        JacksonJocastaCoreConfiguration.class,
        AllControllersConfiguration.class,

        YahtzeeCoreConfiguration.class,
        YahtzeeStatisticsConfiguration.class,
})
public class YahtzeeMain {
    public static void main(String[] args) {
        SpringApplication.run(YahtzeeMain.class, args);
    }
}

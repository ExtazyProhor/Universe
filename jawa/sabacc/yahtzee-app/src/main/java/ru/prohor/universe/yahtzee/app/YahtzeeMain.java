package ru.prohor.universe.yahtzee.app;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.jackson.jodatime.JacksonJodaTimeConfiguration;
import ru.prohor.universe.jocasta.spring.configuration.HolocronConfiguration;
import ru.prohor.universe.jocasta.jwt.ScarifJwtConfiguration;
import ru.prohor.universe.jocasta.spring.configuration.JocastaAutoConfiguration;
import ru.prohor.universe.jocasta.springweb.ExcludeControllersComponentScan;
import ru.prohor.universe.yahtzee.core.YahtzeeCoreConfiguration;
import ru.prohor.universe.yahtzee.offline.YahtzeeOfflineConfiguration;

@Configuration
@ExcludeControllersComponentScan
@Import({
        JocastaAutoConfiguration.class,
        HolocronConfiguration.class,
        ScarifJwtConfiguration.class,
        JacksonJodaTimeConfiguration.class,

        YahtzeeCoreConfiguration.class,
        YahtzeeOfflineConfiguration.class,
})
public class YahtzeeMain {
    public static void main(String[] args) {
        SpringApplication.run(YahtzeeMain.class, args);
    }
}

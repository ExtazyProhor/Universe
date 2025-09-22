package ru.prohor.universe.yahtzee;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.holocron.HolocronConfiguration;
import ru.prohor.universe.jocasta.scarifJwt.ScarifJwtConfiguration;
import ru.prohor.universe.jocasta.spring.configuration.JocastaAutoConfiguration;
import ru.prohor.universe.jocasta.springweb.ExcludeControllersComponentScan;

@Configuration
@ExcludeControllersComponentScan
@Import({
        JocastaAutoConfiguration.class,
        HolocronConfiguration.class,
        ScarifJwtConfiguration.class,
})
public class YahtzeeMain {
    public static void main(String[] args) {
        SpringApplication.run(YahtzeeMain.class, args);
    }
}

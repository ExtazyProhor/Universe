package ru.prohor.universe.yahtzee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.jocasta.holocron.HolocronConfiguration;
import ru.prohor.universe.jocasta.scarifJwt.ScarifJwtConfiguration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = {Controller.class, RestController.class}
        )
})
@Import({
        HolocronConfiguration.class,
        ScarifJwtConfiguration.class,
})
public class YahtzeeMain {
    public static void main(String[] args) {
        SpringApplication.run(YahtzeeMain.class, args);
    }
}

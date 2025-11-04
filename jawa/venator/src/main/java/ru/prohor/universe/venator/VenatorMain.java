package ru.prohor.universe.venator;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.spring.configuration.HolocronConfiguration;
import ru.prohor.universe.jocasta.spring.configuration.JocastaAutoConfiguration;

@Configuration
@ComponentScan
@Import({
        JocastaAutoConfiguration.class,
        HolocronConfiguration.class,
})
public class VenatorMain {
    public static void main(String[] args) {
        SpringApplication.run(VenatorMain.class, args);
    }
}

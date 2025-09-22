package ru.prohor.universe.yoda.configurations;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.holocron.HolocronConfiguration;
import ru.prohor.universe.jocasta.spring.configuration.JocastaAutoConfiguration;

@Configuration
@ComponentScan
@Import({
        JocastaAutoConfiguration.class,
        HolocronConfiguration.class,
})
public class YodaMain {
    public static void main(String[] args) {
        SpringApplication.run(YodaMain.class, args);
    }
}

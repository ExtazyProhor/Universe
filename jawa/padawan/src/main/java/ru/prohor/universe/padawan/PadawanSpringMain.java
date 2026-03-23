package ru.prohor.universe.padawan;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.chopper.client.ChopperClientConfig;
import ru.prohor.universe.jocasta.spring.configuration.HolocronConfiguration;
import ru.prohor.universe.jocasta.spring.configuration.JocastaAutoConfiguration;

@Configuration
@ComponentScan
@Import({
        JocastaAutoConfiguration.class,
        HolocronConfiguration.class,
        ChopperClientConfig.class,
})
public class PadawanSpringMain {
    public static void main(String[] args) {
        SpringApplication.run(PadawanSpringMain.class, args);
    }
}

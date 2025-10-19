package ru.prohor.universe.yahtzee.legacy;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.spring.configuration.HolocronConfiguration;
import ru.prohor.universe.jocasta.spring.configuration.JocastaAutoConfiguration;
import ru.prohor.universe.jocasta.springweb.ExcludeControllersComponentScan;

@Configuration
@ExcludeControllersComponentScan
@Import({
        JocastaAutoConfiguration.class,
        HolocronConfiguration.class,
})
public class YahtzeeLegacyMain {
    public static void main(String[] args) {
        SpringApplication.run(YahtzeeLegacyMain.class, args);
    }
}

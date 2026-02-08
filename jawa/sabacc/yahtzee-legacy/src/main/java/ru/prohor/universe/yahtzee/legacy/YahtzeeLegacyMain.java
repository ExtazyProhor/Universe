package ru.prohor.universe.yahtzee.legacy;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.spring.UniverseEnvironment;
import ru.prohor.universe.jocasta.spring.configuration.HolocronConfiguration;
import ru.prohor.universe.jocasta.spring.configuration.JocastaAutoConfiguration;
import ru.prohor.universe.jocasta.springweb.configuration.FaviconControllerConfiguration;
import ru.prohor.universe.jocasta.springweb.configuration.FilesControllerConfiguration;
import ru.prohor.universe.jocasta.springweb.configuration.RootControllerConfiguration;
import ru.prohor.universe.jocasta.springweb.configuration.StaticResourcesHandlerConfiguration;

@Configuration
@ComponentScan
@Import({
        StaticResourcesHandlerConfiguration.class,
        FilesControllerConfiguration.class,
        FaviconControllerConfiguration.class,
        RootControllerConfiguration.class,

        JocastaAutoConfiguration.class,
        HolocronConfiguration.class,
})
public class YahtzeeLegacyMain {
    public static void main(String[] args) {
        SpringApplication.run(YahtzeeLegacyMain.class, args);
    }
}

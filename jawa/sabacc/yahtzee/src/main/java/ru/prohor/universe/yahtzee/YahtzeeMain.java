package ru.prohor.universe.yahtzee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.jocasta.core.collections.Opt;
import ru.prohor.universe.jocasta.holocron.HolocronConfiguration;
import ru.prohor.universe.jocasta.scarifJwt.ScarifJwtConfiguration;

@Configuration
@EnableAutoConfiguration(
        exclude = {
                MongoAutoConfiguration.class,
                MongoDataAutoConfiguration.class,
                MongoReactiveAutoConfiguration.class,
                MongoReactiveDataAutoConfiguration.class
        }
)
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
        ConfigurableApplicationContext context = SpringApplication.run(YahtzeeMain.class, args);
        Opt<String> environment = Opt.ofNullable(context.getEnvironment().getProperty("universe.yahtzee.environment"));
        if (environment.isEmpty())
            throw new RuntimeException("""
                    Environment can not be empty.
                    Use "universe.yahtzee.environment={environment}" in .properties file"""
            );
        // TODO log
        System.out.println("APPLICATION STARTED WITH ENV=" + environment.get());
    }
}

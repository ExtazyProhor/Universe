package ru.prohor.universe.jocasta.spring.configuration;

import org.fusesource.jansi.Ansi;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import ru.prohor.universe.jocasta.spring.UniverseEnvironment;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Configuration
@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,

        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class,
        MongoReactiveAutoConfiguration.class,
        MongoReactiveDataAutoConfiguration.class,
})
public class JocastaAutoConfiguration {
    private static final String NO_PROFILE_MESSAGE = """
            No spring profile is active.
            Use environment variable or application.properties file""";

    @Bean
    public UniverseEnvironment universeEnvironment(Environment environment) {
        String[] profiles = environment.getActiveProfiles();
        if (profiles.length == 0)
            throw new IllegalStateException(NO_PROFILE_MESSAGE);

        List<UniverseEnvironment> environments = Arrays.stream(profiles)
                .map(UniverseEnvironment::get)
                .filter(Objects::nonNull)
                .toList();

        if (environments.isEmpty())
            throw new IllegalStateException("There is no universe environment");
        if (environments.size() > 1)
            throw new IllegalStateException("There are more than 1 universe environment");
        return environments.getFirst();
    }

    @Bean
    public ApplicationRunner environmentChecker(UniverseEnvironment universeEnvironment) {
        return args -> {
            // TODO заменить на нормальный логгер
            System.out.println(
                    Ansi.ansi()
                            .a("\uD83D\uDFE2 ").fgBrightGreen().bold().a("APPLICATION STARTED WITH ENV=")
                            .fgBrightBlue().a(universeEnvironment.name())
                            .reset()
            );
        };
    }
}

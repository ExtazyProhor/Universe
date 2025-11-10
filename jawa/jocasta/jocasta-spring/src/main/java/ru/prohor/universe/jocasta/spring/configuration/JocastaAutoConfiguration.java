package ru.prohor.universe.jocasta.spring.configuration;

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
    private static final String ENV_PROPERTY = "spring.profiles.active";
    private static final String NO_ENV_MESSAGE = """
            No spring profile is active.
            Use environment variable or application.properties file""";

    @Bean
    public UniverseEnvironment universeEnvironment(Environment environment) {
        String env = environment.getProperty(ENV_PROPERTY);
        if (env == null || env.isBlank())
            throw new IllegalStateException(NO_ENV_MESSAGE);
        return UniverseEnvironment.get(env);
    }

    @Bean
    public ApplicationRunner environmentChecker(UniverseEnvironment universeEnvironment) {
        return args -> {
            // TODO заменить на нормальный логгер
            System.out.println("APPLICATION STARTED WITH ENV=" + universeEnvironment.name());
        };
    }
}

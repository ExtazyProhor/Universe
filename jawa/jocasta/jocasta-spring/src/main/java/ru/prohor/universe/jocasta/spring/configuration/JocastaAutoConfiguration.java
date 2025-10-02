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
import ru.prohor.universe.jocasta.core.collections.common.Opt;

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
    private static final String NO_ENV_MESSAGE = """
            Environment can not be empty.
            Use "spring.profiles.active={environment}" in application.properties file""";

    @Bean
    public ApplicationRunner environmentChecker(Environment environment) {
        return args -> {
            String env = Opt.ofNullable(environment.getProperty("spring.profiles.active"))
                    .filter(s -> !s.isBlank())
                    .orElseThrow(() -> new IllegalStateException(NO_ENV_MESSAGE));
            // TODO заменить на нормальный логгер
            System.out.println("APPLICATION STARTED WITH ENV=" + env);
        };
    }
}

package ru.prohor.universe.jocasta.spring.configuration;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
})
public class JocastaAutoConfiguration {
    private static final String NO_ENV_MESSAGE = """
            Environment can not be empty.
            Use "universe.environment={environment}" in application.properties file""";

    @Bean
    public ApplicationRunner environmentChecker(Environment environment) {
        return args -> {
            String env = environment.getProperty("universe.environment");
            if (env == null || env.isBlank())
                throw new IllegalStateException(NO_ENV_MESSAGE);
            // TODO заменить на нормальный логгер
            System.out.println("APPLICATION STARTED WITH ENV=" + env);
        };
    }
}

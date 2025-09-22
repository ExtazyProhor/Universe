package ru.prohor.universe.yoda.configurations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.holocron.HolocronConfiguration;

@Configuration
@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
})
@ComponentScan
@Import({
        HolocronConfiguration.class,
})
public class YodaMain {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(YodaMain.class, args);
        Opt<String> environment = Opt.ofNullable(context.getEnvironment().getProperty("universe.yoda.environment"));
        if (environment.isEmpty())
            throw new RuntimeException("""
                    Environment can not be empty.
                    Use "universe.yoda.environment={environment}" in .properties file"""
            );
        // TODO log
        System.out.println("APPLICATION STARTED WITH ENV=" + environment.get());
    }
}

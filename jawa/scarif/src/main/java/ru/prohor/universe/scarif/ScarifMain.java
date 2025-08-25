package ru.prohor.universe.scarif;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.jocasta.core.collections.Opt;

@Configuration
@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
})
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = {Controller.class, RestController.class}
        )
})
public class ScarifMain {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ScarifMain.class, args);
        Opt<String> environment = Opt.ofNullable(context.getEnvironment().getProperty("universe.scarif.environment"));
        if (environment.isEmpty())
            throw new RuntimeException("""
                    Environment can not be empty.
                    Use "universe.scarif.environment={environment}" in .properties file"""
            );
        // TODO log
        System.out.println("APPLICATION STARTED WITH ENV=" + environment.get());
    }
}
// TODO java bean validation
// TODO (hibernate validation)
// TODO обработка error-ов тут. Можно вместо error-хэндлера просто вместо 404 возвращать свой файлик параметризованный
// TODO страница профиля

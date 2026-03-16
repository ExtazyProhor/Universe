package ru.prohor.universe.yahtzee.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.prohor.universe.yahtzee.app.services.AdminValidationService;

@Configuration
@Profile("local | testing")
public class LocalConfiguration {
    @Bean
    public AdminValidationService adminValidationService() {
        return player -> player.username().equals("admin");
    }
}

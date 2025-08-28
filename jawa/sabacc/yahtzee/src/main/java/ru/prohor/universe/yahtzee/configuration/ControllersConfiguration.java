package ru.prohor.universe.yahtzee.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.spring.configuration.AllControllersConfiguration;
import ru.prohor.universe.yahtzee.services.UserService;
import ru.prohor.universe.yahtzee.web.controllers.AccountController;
import ru.prohor.universe.yahtzee.web.controllers.GameIrlController;

@Configuration
@Import({
        AllControllersConfiguration.class
})
public class ControllersConfiguration {
    @Bean
    public AccountController accountController(
            UserService userService
    ) {
        return new AccountController(userService);
    }

    @Bean
    public GameIrlController gameIrlController() {
        return new GameIrlController();
    }
}

package ru.prohor.universe.yahtzee.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.spring.configuration.AllControllersConfiguration;
import ru.prohor.universe.yahtzee.services.color.GameColorsService;
import ru.prohor.universe.yahtzee.services.UserService;
import ru.prohor.universe.yahtzee.services.game.irl.IrlGameService;
import ru.prohor.universe.yahtzee.web.controllers.AccountController;
import ru.prohor.universe.yahtzee.web.controllers.GameIrlController;

@Configuration
@Import({
        AllControllersConfiguration.class
})
public class ControllersConfiguration {
    @Bean
    public AccountController accountController(
            GameColorsService gameColorsService,
            UserService userService
    ) {
        return new AccountController(gameColorsService, userService);
    }

    @Bean
    public GameIrlController gameIrlController(
            IrlGameService irlGameService
    ) {
        return new GameIrlController(irlGameService);
    }
}

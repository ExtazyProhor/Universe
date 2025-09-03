package ru.prohor.universe.yahtzee.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.spring.configuration.AllControllersConfiguration;
import ru.prohor.universe.yahtzee.services.AvatarService;
import ru.prohor.universe.yahtzee.services.color.GameColorsService;
import ru.prohor.universe.yahtzee.services.AccountService;
import ru.prohor.universe.yahtzee.services.game.irl.IrlGameService;
import ru.prohor.universe.yahtzee.web.controllers.AccountController;
import ru.prohor.universe.yahtzee.web.controllers.GameIrlController;
import ru.prohor.universe.yahtzee.web.controllers.ImagesController;

@Configuration
@Import({
        AllControllersConfiguration.class
})
public class ControllersConfiguration {
    @Bean
    public AccountController accountController(
            GameColorsService gameColorsService,
            AccountService accountService
    ) {
        return new AccountController(gameColorsService, accountService);
    }

    @Bean
    public GameIrlController gameIrlController(
            IrlGameService irlGameService
    ) {
        return new GameIrlController(irlGameService);
    }

    @Bean
    public ImagesController imagesController(
            AvatarService avatarService
    ) {
        return new ImagesController(avatarService);
    }
}

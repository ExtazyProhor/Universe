package ru.prohor.universe.yahtzee.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.springweb.configuration.AllControllersConfiguration;
import ru.prohor.universe.yahtzee.app.services.OfflineGameInfoService;
import ru.prohor.universe.yahtzee.app.services.images.ImagesService;
import ru.prohor.universe.yahtzee.app.web.controllers.OfflineGameInfoController;
import ru.prohor.universe.yahtzee.core.services.color.GameColorsService;
import ru.prohor.universe.yahtzee.app.services.AccountService;
import ru.prohor.universe.yahtzee.offline.services.OfflineGameService;
import ru.prohor.universe.yahtzee.app.web.controllers.AccountController;
import ru.prohor.universe.yahtzee.app.web.controllers.OfflineGameController;
import ru.prohor.universe.yahtzee.app.web.controllers.ImagesController;

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
    public OfflineGameController offlineGameController(
            OfflineGameService offlineGameService
    ) {
        return new OfflineGameController(offlineGameService);
    }

    @Bean
    public ImagesController imagesController(
            ImagesService imagesService
    ) {
        return new ImagesController(imagesService);
    }

    @Bean
    public OfflineGameInfoController offlineGameInfoController(
            OfflineGameInfoService offlineGameInfoService
    ) {
        return new OfflineGameInfoController(offlineGameInfoService);
    }
}

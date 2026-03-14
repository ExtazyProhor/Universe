package ru.prohor.universe.yahtzee.app.configuration;

import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.yahtzee.app.services.AdminValidationService;
import ru.prohor.universe.yahtzee.app.services.images.ImagesService;
import ru.prohor.universe.yahtzee.core.data.pojo.player.Player;
import ru.prohor.universe.yahtzee.core.services.color.GameColorsService;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Configuration
@Profile("local | testing")
public class LocalConfiguration {
    @Bean
    public AdminValidationService adminValidationService(
            MongoRepository<Player> playersRepository,
            GameColorsService gameColorsService,
            ImagesService imagesService
    ) {
        Player admin = new Player(
                ObjectId.get(),
                UUID.randomUUID(),
                0,
                "admin",
                gameColorsService.getRandomColorId(),
                "Админ",
                List.of(),
                Opt.empty(),
                imagesService.generateAndSave().id(),
                Instant.now(),
                true,
                List.of(),
                List.of()
        );
        playersRepository.save(admin);
        return new AdminValidationService(Set.of(admin.id()));
    }
}

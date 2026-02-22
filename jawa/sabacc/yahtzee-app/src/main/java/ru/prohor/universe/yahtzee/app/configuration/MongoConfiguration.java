package ru.prohor.universe.yahtzee.app.configuration;

import dev.morphia.Datastore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import ru.prohor.universe.jocasta.cfg.morphia.MongoInstanceConfiguration;
import ru.prohor.universe.jocasta.morphia.impl.MongoMorphiaRepository;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.yahtzee.core.data.entities.dto.ImageDto;
import ru.prohor.universe.yahtzee.core.data.entities.dto.PlayerDto;
import ru.prohor.universe.yahtzee.core.data.entities.pojo.Image;
import ru.prohor.universe.yahtzee.core.data.entities.pojo.Player;
import ru.prohor.universe.yahtzee.offline.data.entities.dto.OfflineGameDto;
import ru.prohor.universe.yahtzee.offline.data.entities.dto.OfflineRoomDto;
import ru.prohor.universe.yahtzee.offline.data.entities.pojo.OfflineGame;
import ru.prohor.universe.yahtzee.offline.data.entities.pojo.OfflineRoom;
import ru.prohor.universe.yahtzee.stats.model.OfflineStats;
import ru.prohor.universe.yahtzee.stats.model.OfflineStatsDto;

@Configuration
@Profile("stable | canary")
@Import(MongoInstanceConfiguration.class)
public class MongoConfiguration {
    @Bean
    public MongoRepository<Image> imageRepository(Datastore datastore) {
        return MongoMorphiaRepository.createRepository(datastore, Image.class, ImageDto.class, Image::fromDto);
    }

    @Bean
    public MongoRepository<OfflineGame> offlineGameRepository(Datastore datastore) {
        return MongoMorphiaRepository.createRepository(
                datastore,
                OfflineGame.class,
                OfflineGameDto.class,
                OfflineGame::fromDto
        );
    }

    @Bean
    public MongoRepository<OfflineRoom> offlineRoomRepository(Datastore datastore) {
        return MongoMorphiaRepository.createRepository(
                datastore,
                OfflineRoom.class,
                OfflineRoomDto.class,
                OfflineRoom::fromDto
        );
    }

    @Bean
    public MongoRepository<Player> playerRepository(Datastore datastore) {
        return MongoMorphiaRepository.createRepository(datastore, Player.class, PlayerDto.class, Player::fromDto);
    }

    @Bean
    public MongoRepository<OfflineStats> offlineStatsRepository(Datastore datastore) {
        return MongoMorphiaRepository.createRepository(
                datastore,
                OfflineStats.class,
                OfflineStatsDto.class,
                OfflineStats::fromDto
        );
    }
}

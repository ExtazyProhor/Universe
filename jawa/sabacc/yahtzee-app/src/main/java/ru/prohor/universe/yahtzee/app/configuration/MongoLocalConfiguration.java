package ru.prohor.universe.yahtzee.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import ru.prohor.universe.jocasta.cfg.morphia.MongoInMemoryConfiguration;
import ru.prohor.universe.jocasta.morphia.impl.MongoInMemoryRepository;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.yahtzee.core.data.entities.pojo.Image;
import ru.prohor.universe.yahtzee.core.data.entities.pojo.Player;
import ru.prohor.universe.yahtzee.offline.data.entities.pojo.OfflineGame;
import ru.prohor.universe.yahtzee.offline.data.entities.pojo.OfflineRoom;

@Configuration
@Profile("local | testing")
@Import(MongoInMemoryConfiguration.class)
public class MongoLocalConfiguration {
    @Bean
    public MongoRepository<Image> imageRepository() {
        return new MongoInMemoryRepository<>(Image::id, Image.class);
    }

    @Bean
    public MongoRepository<OfflineGame> offlineGameRepository() {
        return new MongoInMemoryRepository<>(OfflineGame::id, OfflineGame.class);
    }

    @Bean
    public MongoRepository<OfflineRoom> offlineRoomRepository() {
        return new MongoInMemoryRepository<>(OfflineRoom::id, OfflineRoom.class);
    }

    @Bean
    public MongoRepository<Player> playerRepository() {
        return new MongoInMemoryRepository<>(
                Player::id,
                (player, s) -> player.username().equalsIgnoreCase(s) || player.displayName().equalsIgnoreCase(s),
                Player.class
        );
    }
}

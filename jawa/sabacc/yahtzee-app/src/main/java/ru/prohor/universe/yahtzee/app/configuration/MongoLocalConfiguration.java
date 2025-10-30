package ru.prohor.universe.yahtzee.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.prohor.universe.jocasta.morphia.MongoInMemoryRepository;
import ru.prohor.universe.jocasta.morphia.MongoInMemoryTransactionService;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.MongoTransactionService;
import ru.prohor.universe.yahtzee.core.data.entities.pojo.Image;
import ru.prohor.universe.yahtzee.offline.data.entities.pojo.OfflineGame;
import ru.prohor.universe.yahtzee.offline.data.entities.pojo.OfflineRoom;
import ru.prohor.universe.yahtzee.core.data.entities.pojo.Player;

@Configuration
@Profile("local | testing")
public class MongoLocalConfiguration {
    @Bean
    public MongoRepository<Image> imageRepository() {
        return new MongoInMemoryRepository<>(Image::id);
    }

    @Bean
    public MongoRepository<OfflineGame> offlineGameRepository() {
        return new MongoInMemoryRepository<>(OfflineGame::id);
    }

    @Bean
    public MongoRepository<OfflineRoom> offlineRoomRepository() {
        return new MongoInMemoryRepository<>(OfflineRoom::id);
    }

    @Bean
    public MongoRepository<Player> playerRepository() {
        return new MongoInMemoryRepository<>(
                Player::id,
                (player, s) -> player.username().equalsIgnoreCase(s) || player.displayName().equalsIgnoreCase(s)
        );
    }

    @Bean
    public MongoTransactionService transactionService() {
        return new MongoInMemoryTransactionService();
    }
}

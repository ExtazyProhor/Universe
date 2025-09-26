package ru.prohor.universe.yahtzee.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.prohor.universe.jocasta.morphia.MongoInMemoryRepository;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.yahtzee.data.entities.pojo.Image;
import ru.prohor.universe.yahtzee.data.entities.pojo.IrlGame;
import ru.prohor.universe.yahtzee.data.entities.pojo.IrlRoom;
import ru.prohor.universe.yahtzee.data.entities.pojo.Player;

@Configuration
@Profile("local")
public class MongoLocalConfiguration {
    @Bean
    public MongoRepository<Image> imageRepository() {
        return new MongoInMemoryRepository<>(Image::id);
    }

    @Bean
    public MongoRepository<IrlGame> irlGameRepository() {
        return new MongoInMemoryRepository<>(IrlGame::id);
    }

    @Bean
    public MongoRepository<IrlRoom> irlRoomRepository() {
        return new MongoInMemoryRepository<>(IrlRoom::id);
    }

    @Bean
    public MongoRepository<Player> playerRepository() {
        return new MongoInMemoryRepository<>(
                Player::id,
                (player, s) -> player.username().equalsIgnoreCase(s) || player.displayName().equalsIgnoreCase(s)
        );
    }
}

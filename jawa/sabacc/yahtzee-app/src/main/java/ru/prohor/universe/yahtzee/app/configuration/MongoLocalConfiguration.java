package ru.prohor.universe.yahtzee.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import ru.prohor.universe.jocasta.cfg.morphia.MongoInMemoryConfiguration;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.impl.MongoInMemoryRepository;
import ru.prohor.universe.yahtzee.core.data.pojo.game.Game;
import ru.prohor.universe.yahtzee.core.data.pojo.image.Image;
import ru.prohor.universe.yahtzee.core.data.pojo.player.Player;
import ru.prohor.universe.yahtzee.core.data.pojo.room.TactileRoom;
import ru.prohor.universe.yahtzee.stats.model.Stats;

@Configuration
@Profile("local | testing")
@Import(MongoInMemoryConfiguration.class)
public class MongoLocalConfiguration {
    @Bean
    public MongoRepository<Image> imageRepository() {
        return new MongoInMemoryRepository<>(Image::id, Image.class);
    }

    @Bean
    public MongoRepository<Game> gameRepository() {
        return new MongoInMemoryRepository<>(Game::id, Game.class);
    }

    @Bean
    public MongoRepository<TactileRoom> tactileRoomRepository() {
        return new MongoInMemoryRepository<>(TactileRoom::id, TactileRoom.class);
    }

    @Bean
    public MongoRepository<Player> playerRepository() {
        return new MongoInMemoryRepository<>(
                Player::id,
                (player, s) -> player.username().equalsIgnoreCase(s) || player.displayName().equalsIgnoreCase(s),
                Player.class
        );
    }

    @Bean
    public MongoRepository<Stats> offlineStatsRepository() {
        return new MongoInMemoryRepository<>(Stats::id, Stats.class);
    }
}

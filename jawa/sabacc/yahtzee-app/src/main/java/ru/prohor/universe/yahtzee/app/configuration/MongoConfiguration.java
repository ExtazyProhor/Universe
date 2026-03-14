package ru.prohor.universe.yahtzee.app.configuration;

import dev.morphia.Datastore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import ru.prohor.universe.jocasta.cfg.morphia.MongoInstanceConfiguration;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.impl.MongoMorphiaRepository;
import ru.prohor.universe.yahtzee.core.data.dto.game.GameDto;
import ru.prohor.universe.yahtzee.core.data.dto.image.ImageDto;
import ru.prohor.universe.yahtzee.core.data.dto.player.PlayerDto;
import ru.prohor.universe.yahtzee.core.data.dto.room.TactileRoomDto;
import ru.prohor.universe.yahtzee.core.data.pojo.game.Game;
import ru.prohor.universe.yahtzee.core.data.pojo.image.Image;
import ru.prohor.universe.yahtzee.core.data.pojo.player.Player;
import ru.prohor.universe.yahtzee.core.data.pojo.room.TactileRoom;
import ru.prohor.universe.yahtzee.stats.model.Stats;
import ru.prohor.universe.yahtzee.stats.model.StatsDto;

@Configuration
@Profile("stable | canary")
@Import(MongoInstanceConfiguration.class)
public class MongoConfiguration {
    @Bean
    public MongoRepository<Image> imageRepository(Datastore datastore) {
        return MongoMorphiaRepository.createRepository(datastore, Image.class, ImageDto.class, Image::fromDto);
    }

    @Bean
    public MongoRepository<Game> gameRepository(Datastore datastore) {
        return MongoMorphiaRepository.createRepository(
                datastore,
                Game.class,
                GameDto.class,
                Game::fromDto
        );
    }

    @Bean
    public MongoRepository<TactileRoom> tactileRoomRepository(Datastore datastore) {
        return MongoMorphiaRepository.createRepository(
                datastore,
                TactileRoom.class,
                TactileRoomDto.class,
                TactileRoom::fromDto
        );
    }

    @Bean
    public MongoRepository<Player> playerRepository(Datastore datastore) {
        return MongoMorphiaRepository.createRepository(datastore, Player.class, PlayerDto.class, Player::fromDto);
    }

    @Bean
    public MongoRepository<Stats> statsRepository(Datastore datastore) {
        return MongoMorphiaRepository.createRepository(
                datastore,
                Stats.class,
                StatsDto.class,
                Stats::fromDto
        );
    }
}

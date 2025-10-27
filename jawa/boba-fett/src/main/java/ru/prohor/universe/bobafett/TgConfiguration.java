package ru.prohor.universe.bobafett;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prohor.universe.jocasta.tgbots.BotSettings;
import ru.prohor.universe.jocasta.tgbots.RegisterBot;

@Configuration
public class TgConfiguration {
    @Bean
    public BotSettings botSettings(
            @Value("${universe.boba-fett.bot-token}") String token,
            @Value("${universe.boba-fett.bot-username}") String username
    ) {
        return BotSettings.builder(token, username).build();
    }

    @Bean
    public BobaFettBot bobaFettBot(BotSettings settings) {
        return RegisterBot.register(new BobaFettBot(settings));
    }
}

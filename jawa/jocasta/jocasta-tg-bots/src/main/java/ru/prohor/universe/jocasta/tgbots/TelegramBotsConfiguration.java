package ru.prohor.universe.jocasta.tgbots;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;

import java.util.List;

@Configuration
public class TelegramBotsConfiguration {
    @Bean(destroyMethod = "close")
    public TelegramBotsLongPollingApplication telegramBotsApplication(List<DeafBot> bots) {
        TelegramBotsLongPollingApplication telegramApplication = new TelegramBotsLongPollingApplication();
        bots.forEach(bot -> Sneaky.execute(() -> {
            telegramApplication.registerBot(bot.getToken(), bot);
            return bot;
        }));
        return telegramApplication;
    }
}

package ru.prohor.universe.jocasta.tgbots;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;

public class RegisterBot {
    public static <T extends SimpleBot> T register(T bot) {
        return Sneaky.execute(() -> {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);
            return bot;
        });
    }
}

package ru.prohor.universe.jocasta.tgbots.api;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.prohor.universe.jocasta.tgbots.bot.TgBot;

public abstract class TgBotCallback implements Identifiable<String> {
    protected final String callbackPrefix;

    public TgBotCallback(String callbackPrefix) {
        this.callbackPrefix = callbackPrefix;
    }

    public abstract void callbackReceived(CallbackQuery callbackQuery, TgBot bot) throws Exception;

    protected final String getSuffix(CallbackQuery callbackQuery) {
        return callbackQuery.getData().substring(getIdentifier().length());
    }

    @Override
    public final String getIdentifier() {
        return callbackPrefix;
    }
}

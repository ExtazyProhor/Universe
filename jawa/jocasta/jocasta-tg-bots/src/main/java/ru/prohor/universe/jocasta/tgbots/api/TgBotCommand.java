package ru.prohor.universe.jocasta.tgbots.api;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.jocasta.tgbots.bot.TgBot;

public abstract class TgBotCommand implements Identifiable<String> {
    private final String command;
    private final String description;

    public TgBotCommand(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public abstract void executeCommand(Message message, TgBot bot) throws Exception;

    public final String getDescription() {
        return description;
    }

    @Override
    public final String getIdentifier() {
        return command;
    }
}

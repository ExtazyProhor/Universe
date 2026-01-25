package ru.prohor.universe.yoda.bot;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.jocasta.tgbots.BotSettings;
import ru.prohor.universe.jocasta.tgbots.SimpleBot;
import ru.prohor.universe.yoda.log.FileLogger;
import ru.prohor.universe.yoda.log.LogLevel;

public class YodaBot extends SimpleBot {
    private final FileLogger logger;

    public YodaBot(BotSettings settings, FileLogger logger) {
        super(settings);
        this.logger = logger;
    }

    @Override
    public void onHandlingException(Exception e) {
        logger.log(LogLevel.ERROR, "error while handling", e);
    }

    @Override
    public void onSendingException(Exception e, long chatId) {
        logger.log(LogLevel.ERROR, "error while sending to chatId=" + chatId, e);
    }

    @Override
    public void onBotAddedToChat(long chatId, Chat chat) {
        // TODO
    }

    @Override
    public void onBotRemovedFromChat(long chatId, Chat chat) {
        // TODO
    }

    @Override
    public void onUnrecognizedChatMember(long chatId, ChatMemberUpdated chatMemberUpdated) {
        logger.log(LogLevel.WARN, "unrecognized chatMember: " + chatMemberUpdated); // TODO
    }

    @Override
    public void onUnknownAction(Update update) {
        logger.log(LogLevel.WARN, "unknown update: " + update); // TODO
    }

    @Override
    public void onForbidden(String response, long chatId) {
        logger.log(LogLevel.WARN, "forbidden for chatId=" + chatId + ", " + response); // TODO
    }

    @Override
    public void onMigrateToSuperGroup(long oldChatId, long newChatId) {
        logger.log(LogLevel.WARN, "migrate from " + oldChatId + " to " + newChatId); // TODO
    }
}

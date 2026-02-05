package ru.prohor.universe.yoda.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.jocasta.tgbots.BotSettings;
import ru.prohor.universe.jocasta.tgbots.SimpleBot;

public class YodaBot extends SimpleBot {
    private static final Logger log = LoggerFactory.getLogger(YodaBot.class);

    public YodaBot(BotSettings settings) {
        super(settings);
    }

    @Override
    public void onHandlingException(Exception e) {
        log.error("error while handling", e);
    }

    @Override
    public void onSendingException(Exception e, long chatId) {
        log.error("error while sending to chatId={}", chatId, e);
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
        log.warn("unrecognized chatMember: {}", chatMemberUpdated);
    }

    @Override
    public void onUnknownAction(Update update) {
        log.warn("unknown update: " + update);
    }

    @Override
    public void onForbidden(String response, long chatId) {
        log.warn("forbidden for chatId={}, {}", chatId, response);
    }

    @Override
    public void onMigrateToSuperGroup(long oldChatId, long newChatId) {
        log.warn("migrate from {} to {}", oldChatId, newChatId);
    }
}

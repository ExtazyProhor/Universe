package ru.prohor.universe.yoda.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
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
    public void onSendingException(Exception e, String chatId) {
        logger.log(LogLevel.ERROR, "error while sending", e);
    }

    @Override
    public void onMessage(Message message) {
        if (message.getChat().isUserChat()) {
            feedbackExecutor.sendMessage(
                    SendMessage.builder()
                            .chatId(message.getChat().getId())
                            .text("Я не знаю что делать с сообщением \"" + message.getText() + "\"")
                            .build()
            );
        }
    }

    @Override
    public void onCallback(CallbackQuery callbackQuery) {}

    @Override
    public void onMyChatMember(ChatMemberUpdated chatMemberUpdated) {}

    @Override
    public void onUnknownAction(Update update) {
        logger.log(LogLevel.WARN, "unknown update: " + update); // TODO
    }

    @Override
    public void onUserDeactivated(TelegramApiException e, String chatId) {
        logger.log(LogLevel.WARN, "chatId=" + chatId, e); // TODO
    }

    @Override
    public void onBotBlockedByUser(TelegramApiException e, String chatId) {
        logger.log(LogLevel.WARN, "chatId=" + chatId, e); // TODO
    }

    @Override
    public void onGroupWasUpgradedToSupergroup(TelegramApiException e, String chatId) {
        logger.log(LogLevel.WARN, "chatId=" + chatId, e); // TODO
    }
}

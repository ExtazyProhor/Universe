package ru.prohor.universe.jocasta.tgbots.bot;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collection;

public interface Bot {
    void onHandlingException(Exception e);

    void onSendingException(Exception e, String chatId);

    void onMessage(Message message);

    void onCallback(CallbackQuery callbackQuery);

    void onMyChatMember(ChatMemberUpdated chatMemberUpdated);

    void onUnknownAction(Update update);

    Collection<BotCommand> listCommands();

    default void onUserDeactivated(TelegramApiException e, String chatId) {
        // TODO log.warn
    }

    default void onBotBlockedByUser(TelegramApiException e, String chatId) {
        // TODO log.warn
    }

    default void onGroupWasUpgradedToSupergroup(TelegramApiException e, String chatId) {
        // TODO log.warn
    }
}

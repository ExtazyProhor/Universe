package ru.prohor.universe.jocasta.tgbots.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.prohor.universe.jocasta.core.features.sneaky.ThrowableRunnable;

public abstract class TgBot extends TelegramLongPollingBot implements Bot {
    protected final String username;

    protected TgBot(
            String token,
            String username
    ) {
        super(token);
        this.username = username;
    }

    @Override
    public final String getBotUsername() {
        return username;
    }

    @Override
    public final void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText())
                onMessage(update.getMessage());
            else if (update.hasCallbackQuery())
                onCallback(update.getCallbackQuery());
            else if (update.hasMyChatMember())
                onMyChatMember(update.getMyChatMember());
            else
                onUnknownAction(update);
        } catch (Exception e) {
            onHandlingException(e);
        }
    }

    public final void sendMessage(SendMessage message) {
        executeSending(() -> execute(message), message.getChatId());
    }

    public final void editMessageText(EditMessageText message) {
        executeSending(() -> execute(message), message.getChatId());
    }

    private void executeSending(ThrowableRunnable task, String chatId) {
        try {
            task.run();
        } catch (TelegramApiException e) {
            String message = e.getMessage();
            if (message.contains("user is deactivated"))
                onUserDeactivated(e, chatId);
            else if (message.contains("bot was blocked by the user"))
                onBotBlockedByUser(e, chatId);
            else if (message.contains("group chat was upgraded to a supergroup chat"))
                onGroupWasUpgradedToSupergroup(e, chatId);
            else
                onSendingException(e, chatId);
        } catch (Exception e) {
            onSendingException(e, chatId);
        }
    }
}

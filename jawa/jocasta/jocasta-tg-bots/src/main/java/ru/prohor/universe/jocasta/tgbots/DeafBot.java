package ru.prohor.universe.jocasta.tgbots;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.prohor.universe.jocasta.core.features.sneaky.ThrowableRunnable;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

public abstract class DeafBot extends TelegramLongPollingBot {
    protected final FeedbackExecutor feedbackExecutor;
    protected final String username;

    public DeafBot(BotAuth auth) {
        super(auth.token());
        this.feedbackExecutor = makeFeedbackExecutor();
        this.username = auth.username();
    }

    public FeedbackExecutor getFeedbackExecutor() {
        return feedbackExecutor;
    }

    private FeedbackExecutor makeFeedbackExecutor() {
        return new FeedbackExecutor() {
            @Override
            public void sendMessage(SendMessage message) {
                executeSending(() -> execute(message), message.getChatId());
            }

            @Override
            public void editMessageText(EditMessageText message) {
                executeSending(() -> execute(message), message.getChatId());
            }
        };
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

    @Override
    public final String getBotUsername() {
        return username;
    }

    @Override
    public void onUpdateReceived(Update update) {}

    public abstract void onSendingException(Exception e, String chatId);

    public void onUserDeactivated(TelegramApiException e, String chatId) {
        // TODO log.warn
    }

    public void onBotBlockedByUser(TelegramApiException e, String chatId) {
        // TODO log.warn
    }

    public void onGroupWasUpgradedToSupergroup(TelegramApiException e, String chatId) {
        // TODO log.warn
    }
}

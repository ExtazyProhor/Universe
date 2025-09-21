package ru.prohor.universe.jocasta.tgbots;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.prohor.universe.jocasta.core.features.sneaky.ThrowableRunnable;
import ru.prohor.universe.jocasta.tgbots.api.Bot;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.support.FeatureSupport;

public abstract class SimpleBot extends TelegramLongPollingBot implements Bot {
    protected final FeedbackExecutor feedbackExecutor;
    protected final String username;

    private final FeatureSupport<Message> commandSupport;
    private final FeatureSupport<CallbackQuery> callbackSupport;
    private final FeatureSupport<Update> statusSupport;

    protected SimpleBot(BotSettings settings) {
        super(settings.token);
        this.feedbackExecutor = makeFeedbackExecutor();
        this.username = settings.username;
        this.commandSupport = settings.commandSupport;
        this.callbackSupport = settings.callbackSupport;
        this.statusSupport = settings.statusSupport;
    }

    @Override
    public final String getBotUsername() {
        return username;
    }

    @Override
    public final void onUpdateReceived(Update update) {
        try {
            if (!statusSupport.handle(update, feedbackExecutor))
                return;

            if (update.hasMessage() && update.getMessage().hasText()) {
                Message message = update.getMessage();
                if (commandSupport.handle(message, feedbackExecutor))
                    onMessage(message);
                return;
            }
            if (update.hasCallbackQuery()) {
                CallbackQuery callback = update.getCallbackQuery();
                if (callbackSupport.handle(callback, feedbackExecutor))
                    onCallback(callback);
                return;
            }
            if (update.hasMyChatMember()) {
                onMyChatMember(update.getMyChatMember());
                return;
            }
            onUnknownAction(update);
        } catch (Exception e) {
            onHandlingException(e);
        }
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
}

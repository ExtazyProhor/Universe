package ru.prohor.universe.jocasta.tgbots;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.prohor.universe.jocasta.tgbots.support.FeatureSupport;

public abstract class SimpleBot extends DeafBot {
    private final FeatureSupport<Message> commandSupport;
    private final FeatureSupport<CallbackQuery> callbackSupport;
    private final FeatureSupport<Update> statusSupport;

    protected SimpleBot(BotSettings settings) {
        super(settings.auth);
        this.commandSupport = settings.commandSupport;
        this.callbackSupport = settings.callbackSupport;
        this.statusSupport = settings.statusSupport;
    }

    public abstract void onHandlingException(Exception e);

    public abstract void onMessage(Message message);

    public abstract void onCallback(CallbackQuery callbackQuery);

    public abstract void onMyChatMember(ChatMemberUpdated chatMemberUpdated);

    public abstract void onUnknownAction(Update update);

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
                suppressTimer(callback);
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

    private void suppressTimer(CallbackQuery callback) throws TelegramApiException {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callback.getId());
        execute(answer);
    }
}

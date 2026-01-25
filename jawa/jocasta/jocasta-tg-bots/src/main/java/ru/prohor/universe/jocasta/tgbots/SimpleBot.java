package ru.prohor.universe.jocasta.tgbots;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusFlow;
import ru.prohor.universe.jocasta.tgbots.support.FeatureSupport;
import ru.prohor.universe.jocasta.tgbots.support.StatusSupport;

public abstract class SimpleBot extends DeafBot {
    private final FeatureSupport<Message> commandSupport;
    private final FeatureSupport<CallbackQuery> callbackSupport;
    private final StatusSupport statusSupport;

    protected SimpleBot(BotSettings settings) {
        super(settings.auth);
        this.commandSupport = settings.commandSupport;
        this.callbackSupport = settings.callbackSupport;
        this.statusSupport = settings.statusSupport;
    }

    public abstract void onHandlingException(Exception e);

    public abstract void onBotAddedToChat(long chatId, Chat chat);

    public abstract void onBotRemovedFromChat(long chatId, Chat chat);

    public abstract void onUnrecognizedChatMember(long chatId, ChatMemberUpdated chatMemberUpdated);

    public abstract void onUnknownAction(Update update);

    @Override
    public final void onUpdateReceived(Update update) {
        try {
            if (statusSupport.handle(update, feedbackExecutor) == StatusFlow.EXIT)
                return;

            if (update.hasMessage()) {
                Message message = update.getMessage();
                if (message.getMigrateToChatId() != null) {
                    long oldChatId = message.getChatId();
                    long newChatId = message.getMigrateToChatId();
                    onMigrateToSuperGroup(oldChatId, newChatId);
                    return;
                }
                if (message.hasText()) {
                    commandSupport.handle(message, feedbackExecutor);
                    return;
                }
            }
            if (update.hasCallbackQuery()) {
                CallbackQuery callback = update.getCallbackQuery();
                suppressTimer(callback);
                callbackSupport.handle(callback, feedbackExecutor);
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

    private void onMyChatMember(ChatMemberUpdated chatMemberUpdated) {
        String oldStatus = chatMemberUpdated.getOldChatMember().getStatus();
        String newStatus = chatMemberUpdated.getNewChatMember().getStatus();
        Chat chat = chatMemberUpdated.getChat();
        long chatId = chat.getId();

        if (oldStatus.equals("left") && newStatus.equals("member")) {
            onBotAddedToChat(chatId, chatMemberUpdated.getChat());
        } else if (newStatus.equals("left") || newStatus.equals("kicked")) {
            onBotRemovedFromChat(chatId, chat);
        } else {
            onUnrecognizedChatMember(chatId, chatMemberUpdated);
        }
    }

    private void suppressTimer(CallbackQuery callback) throws TelegramApiException {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callback.getId());
        execute(answer);
    }
}

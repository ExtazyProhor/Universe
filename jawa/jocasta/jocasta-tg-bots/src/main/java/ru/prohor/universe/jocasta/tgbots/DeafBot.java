package ru.prohor.universe.jocasta.tgbots;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.sneaky.ThrowableConsumer;
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
            public synchronized void sendMessage(SendMessage message) {
                executeSending(
                        () -> execute(message),
                        Opt.of(chatId -> {
                            message.setChatId(chatId);
                            execute(message);
                        }),
                        message.getChatId()
                );
            }

            @Override
            public synchronized void editMessageText(EditMessageText message) {
                executeSending(
                        () -> execute(message),
                        Opt.of(chatId -> {
                            message.setChatId(chatId);
                            execute(message);
                        }),
                        message.getChatId()
                );
            }

            @Override
            public synchronized void sendDocument(SendDocument document) {
                executeSending(
                        () -> execute(document),
                        Opt.of(chatId -> {
                            document.setChatId(chatId);
                            execute(document);
                        }),
                        document.getChatId()
                );
            }
        };
    }

    private void executeSending(
            ThrowableRunnable task,
            Opt<ThrowableConsumer<Long>> taskWithNewChatId,
            String chatId
    ) {
        long numericChatId = Long.parseLong(chatId);
        try {
            task.run();
        } catch (TelegramApiRequestException e) {
            int code = e.getErrorCode();
            String response = e.getApiResponse();
            Long newChatId = e.getParameters().getMigrateToChatId();

            if (newChatId != null && taskWithNewChatId.isPresent()) {
                onMigrateToSuperGroup(Long.parseLong(chatId), newChatId);
                executeSending(() -> taskWithNewChatId.get().accept(newChatId), Opt.empty(), newChatId.toString());
            } else if (code == 403) {
                onForbidden(response, numericChatId);
            } else if (code == 400) {
                if (response.contains("message is not modified")) {
                    // TODO log info / debug
                    return;
                }
                // TODO log warn
            } else if (code == 429) {
                // TODO log too many requests
            } else {
                onSendingException(e, numericChatId);
            }
        } catch (Exception e) {
            onSendingException(e, numericChatId);
        }
    }

    @Override
    public final String getBotUsername() {
        return username;
    }

    @Override
    public void onUpdateReceived(Update update) {}

    public abstract void onSendingException(Exception e, long chatId);

    public abstract void onForbidden(String response, long chatId);

    public abstract void onMigrateToSuperGroup(long oldChatId, long newChatId);
}

package ru.prohor.universe.jocasta.tgbots;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.DefaultLongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.ResponseParameters;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.sneaky.ThrowableConsumer;
import ru.prohor.universe.jocasta.core.features.sneaky.ThrowableRunnable;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

public abstract class DeafBot extends DefaultLongPollingUpdateConsumer {
    protected final FeedbackExecutor feedbackExecutor;
    protected final String username;
    protected final String token;
    protected final TelegramClient telegramClient;

    public DeafBot(BotAuth auth) {
        this.feedbackExecutor = makeFeedbackExecutor();
        this.username = auth.username();
        this.token = auth.token();
        this.telegramClient = new OkHttpTelegramClient(auth.token());
    }

    public String getToken() {
        return token;
    }

    public FeedbackExecutor getFeedbackExecutor() {
        return feedbackExecutor;
    }

    private FeedbackExecutor makeFeedbackExecutor() {
        return new FeedbackExecutor() {
            @Override
            public synchronized void sendMessage(SendMessage message) {
                executeSending(
                        () -> telegramClient.execute(message),
                        Opt.of(chatId -> {
                            message.setChatId(chatId);
                            telegramClient.execute(message);
                        }),
                        message.getChatId()
                );
            }

            @Override
            public synchronized void editMessageText(EditMessageText message) {
                executeSending(
                        () -> telegramClient.execute(message),
                        Opt.of(chatId -> {
                            message.setChatId(chatId);
                            telegramClient.execute(message);
                        }),
                        message.getChatId()
                );
            }

            @Override
            public synchronized void sendDocument(SendDocument document) {
                executeSending(
                        () -> telegramClient.execute(document),
                        Opt.of(chatId -> {
                            document.setChatId(chatId);
                            telegramClient.execute(document);
                        }),
                        document.getChatId()
                );
            }

            @Override
            public void sendPhoto(SendPhoto photo) {
                executeSending(
                        () -> telegramClient.execute(photo),
                        Opt.of(chatId -> {
                            photo.setChatId(chatId);
                            telegramClient.execute(photo);
                        }),
                        photo.getChatId()
                );
            }
        };
    }

    // TODO возвращать статус, чтобы можно было сделать ретрай
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
            Long newChatId = Opt.ofNullable(e.getParameters()).map(ResponseParameters::getMigrateToChatId).orElseNull();

            if (newChatId != null && taskWithNewChatId.isPresent()) {
                onMigrateToSuperGroup(Long.parseLong(chatId), newChatId);
                executeSending(() -> taskWithNewChatId.get().accept(newChatId), Opt.empty(), newChatId.toString());
            } else if (code == 403) {
                onForbidden(response, numericChatId);
            } else if (code == 400) {
                if (response != null && response.contains("message is not modified")) {
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
    public void consume(Update update) {}

    public abstract void onSendingException(Exception e, long chatId);

    public abstract void onForbidden(String response, long chatId);

    public abstract void onMigrateToSuperGroup(long oldChatId, long newChatId);
}

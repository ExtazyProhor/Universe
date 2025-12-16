package ru.prohor.universe.bobafett.feature.holidays.callback;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import ru.prohor.universe.bobafett.data.MongoStatusStorage;
import ru.prohor.universe.bobafett.feature.holidays.status.WaitImportChatId;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.callback.CallbackHandler;
import ru.prohor.universe.jocasta.tgbots.api.status.ValuedStatusStorage;

@Service
public class ImportHolidaysInitCallback implements CallbackHandler {
    private final MongoStatusStorage mongoStatusStorage;
    private final WaitImportChatId waitImportChatId;

    public ImportHolidaysInitCallback(MongoStatusStorage mongoStatusStorage, WaitImportChatId waitImportChatId) {
        this.mongoStatusStorage = mongoStatusStorage;
        this.waitImportChatId = waitImportChatId;
    }

    @Override
    public String callback() {
        return "holidays/init-import";
    }

    @Override
    public boolean handle(MaybeInaccessibleMessage message, FeedbackExecutor feedbackExecutor) {
        long chatId = message.getChatId();
        mongoStatusStorage.setStatus(
                chatId,
                new ValuedStatusStorage.ValuedStatus<>(waitImportChatId.key(), Opt.empty())
        );
        EditMessageText editMessageText = EditMessageText.builder()
                .text("Напишите ID пользователя, у которого вы хотите импортировать " +
                        "праздники (узнать ID можно командой /id)")
                .messageId(message.getMessageId())
                .chatId(chatId)
                .build();
        feedbackExecutor.editMessageText(editMessageText);
        return false;
    }
}

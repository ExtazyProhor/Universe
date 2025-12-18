package ru.prohor.universe.bobafett.feature.holidays.callback;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import ru.prohor.universe.bobafett.command.GetIdCommand;
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
    private final String idRequestMessage;

    public ImportHolidaysInitCallback(
            MongoStatusStorage mongoStatusStorage,
            WaitImportChatId waitImportChatId,
            GetIdCommand getIdCommand
    ) {
        this.mongoStatusStorage = mongoStatusStorage;
        this.waitImportChatId = waitImportChatId;
        this.idRequestMessage = "Напишите ID пользователя, у которого вы хотите импортировать " +
                "праздники (узнать ID можно командой " + getIdCommand.command() + ")";
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
        feedbackExecutor.editMessageText(chatId, message.getMessageId(), idRequestMessage);
        return false;
    }
}

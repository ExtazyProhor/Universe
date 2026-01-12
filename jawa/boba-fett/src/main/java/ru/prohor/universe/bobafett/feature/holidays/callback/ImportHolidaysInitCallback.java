package ru.prohor.universe.bobafett.feature.holidays.callback;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import ru.prohor.universe.bobafett.callback.Callbacks;
import ru.prohor.universe.bobafett.command.Commands;
import ru.prohor.universe.bobafett.data.pojo.UserStatus;
import ru.prohor.universe.bobafett.feature.holidays.status.WaitImportChatId;
import ru.prohor.universe.bobafett.service.BobaFettUserService;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.callback.CallbackHandler;

@Service
public class ImportHolidaysInitCallback implements CallbackHandler {
    private static final String ID_REQUEST_MESSAGE = "Напишите ID пользователя, у которого вы хотите импортировать " +
            "праздники (узнать ID можно командой " + Commands.GET_ID + ")";

    private final BobaFettUserService bobaFettUserService;
    private final WaitImportChatId waitImportChatId;

    public ImportHolidaysInitCallback(
            BobaFettUserService bobaFettUserService,
            WaitImportChatId waitImportChatId
    ) {
        this.bobaFettUserService = bobaFettUserService;
        this.waitImportChatId = waitImportChatId;
    }

    @Override
    public String callback() {
        return Callbacks.IMPORT_HOLIDAYS_INIT;
    }

    @Override
    public boolean handle(MaybeInaccessibleMessage message, FeedbackExecutor feedbackExecutor) {
        long chatId = message.getChatId();
        UserStatus status = new UserStatus(waitImportChatId.key(), Opt.empty());
        bobaFettUserService.setStatus(chatId, status);
        feedbackExecutor.editMessageText(chatId, message.getMessageId(), ID_REQUEST_MESSAGE);
        return false;
    }
}

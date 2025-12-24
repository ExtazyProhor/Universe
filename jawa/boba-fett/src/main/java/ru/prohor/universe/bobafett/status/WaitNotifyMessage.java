package ru.prohor.universe.bobafett.status;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.bobafett.command.Commands;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusHandler;

import java.util.ArrayList;
import java.util.List;

@Service
public class WaitNotifyMessage implements StatusHandler<String> {
    @Override
    public String key() {
        return "admin/wait-notify-message";
    }

    @Override
    public boolean handle(Update update, FeedbackExecutor feedbackExecutor) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return true;
        }
        long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText();
        if (message.equals(Commands.CANCEL)) {
            feedbackExecutor.sendMessage(chatId, "Отправка сообщения отменена");
            return false;
        }
        int lineIndex = message.indexOf('\n');
        if (lineIndex < 0) {
            feedbackExecutor.sendMessage(chatId, "Неверный формат, должно быть как минимум 2 строки");
            return false;
        }
        List<Long> chatIds = new ArrayList<>();
        for (String part : message.substring(0, lineIndex).split(",")) {
            try {
                chatIds.add(Long.parseLong(part));
            } catch (NumberFormatException nfe) {
                feedbackExecutor.sendMessage(chatId, "Неверный формат chatId: \"" + part + "\"");
                return false;
            }
        }
        message = message.substring(lineIndex + 1);
        for (Long chat : chatIds) {
            feedbackExecutor.sendMessage(chat, message);
        }
        return false;
    }
}

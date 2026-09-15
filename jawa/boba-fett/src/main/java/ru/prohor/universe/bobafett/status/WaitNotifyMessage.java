package ru.prohor.universe.bobafett.status;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.bobafett.command.Commands;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.jocasta.core.features.fieldref.FR;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilters;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusFlow;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusHandler;

import java.util.ArrayList;
import java.util.List;

@Service
public class WaitNotifyMessage implements StatusHandler<String> {
    private final MongoRepository<BobaFettUser> bobaFettUsersRepository;

    public WaitNotifyMessage(MongoRepository<BobaFettUser> bobaFettUsersRepository) {
        this.bobaFettUsersRepository = bobaFettUsersRepository;
    }

    @Override
    public String key() {
        return "admin/wait-notify-message";
    }

    @Override
    public StatusFlow handle(Update update, FeedbackExecutor feedbackExecutor) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return StatusFlow.CONTINUE;
        }
        long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText();
        if (message.equals(Commands.CANCEL)) {
            feedbackExecutor.sendMessage(chatId, "Отправка сообщения отменена");
            return StatusFlow.EXIT;
        }
        int lineIndex = message.indexOf('\n');
        if (lineIndex < 0) {
            feedbackExecutor.sendMessage(chatId, "Неверный формат, должно быть как минимум 2 строки");
            return StatusFlow.EXIT;
        }
        List<Long> chatIds = new ArrayList<>();
        String chatIdsLine = message.substring(0, lineIndex);
        if (chatIdsLine.equals("all-users")) {
            List<Long> ids = bobaFettUsersRepository.find(MongoFilters.eq(FR.wrap(BobaFettUser::enabled), true))
                    .stream()
                    .map(BobaFettUser::chatId)
                    .toList();
            chatIds.addAll(ids);
        } else {
            for (String part : chatIdsLine.split(",")) {
                try {
                    chatIds.add(Long.parseLong(part));
                } catch (NumberFormatException nfe) {
                    feedbackExecutor.sendMessage(chatId, "Неверный формат chatId: \"" + part + "\"");
                    return StatusFlow.EXIT;
                }
            }
        }
        message = message.substring(lineIndex + 1);
        for (Long chat : chatIds) {
            try {
                feedbackExecutor.sendMessage(chat, message);
            } catch (Exception e) {
                e.printStackTrace(); // TODO log
            }
        }
        return StatusFlow.EXIT;
    }
}

package ru.prohor.universe.bobafett.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler;

@Service
public class CancelCommandHandler implements CommandHandler {
    @Override
    public String command() {
        return Commands.CANCEL;
    }

    @Override
    public Opt<String> description() {
        return Opt.of("отменить действие");
    }

    @Override
    public void handle(Message message, FeedbackExecutor feedbackExecutor) {
        feedbackExecutor.sendMessage(message.getChatId(), "Отменять нечего");
    }
}

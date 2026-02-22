package ru.prohor.universe.bobafett.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler;

@Service
public class GetIdCommand implements CommandHandler {
    @Override
    public String command() {
        return "/id";
    }

    @Override
    public Opt<String> description() {
        return Opt.of("узнать мой id");
    }

    @Override
    public void handle(Message message, FeedbackExecutor feedbackExecutor) {
        long chatId = message.getChatId();
        feedbackExecutor.sendMessage(chatId, String.valueOf(chatId));
    }
}

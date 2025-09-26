package ru.prohor.universe.yoda.bot.handlers.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler;
import ru.prohor.universe.yoda.bot.Text;

@Service
public class StartCommandHandler implements CommandHandler {
    @Override
    public String command() {
        return "/start";
    }

    @Override
    public String description() {
        return Text.CommandDescriptions.start();
    }

    @Override
    public boolean handle(Message message, FeedbackExecutor feedbackExecutor) {
        feedbackExecutor.sendMessage(message.getChatId(), Text.CommandReplies.start());
        return false;
    }
}

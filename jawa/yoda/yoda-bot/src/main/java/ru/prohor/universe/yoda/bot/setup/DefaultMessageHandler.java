package ru.prohor.universe.yoda.bot.setup;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.comand.NonCommandMessageHandler;
import ru.prohor.universe.yoda.bot.Text;

@Service
public class DefaultMessageHandler implements NonCommandMessageHandler {
    @Override
    public void onNonCommandMessage(Message message, FeedbackExecutor feedbackExecutor) {
        if (message.getChat().isUserChat()) {
            feedbackExecutor.sendMessage(
                    message.getChatId(),
                    Text.CommandReplies.nonCommand(message.getText())
            );
        }
    }
}

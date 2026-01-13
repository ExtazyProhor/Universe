package ru.prohor.universe.bobafett.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.service.BobaFettUserService;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler;

@Service
public class StartCommand implements CommandHandler {
    private final BobaFettUserService bobaFettUserService;

    public StartCommand(BobaFettUserService bobaFettUserService) {
        this.bobaFettUserService = bobaFettUserService;
    }

    @Override
    public String command() {
        return "/start";
    }

    @Override
    public boolean handle(Message message, FeedbackExecutor feedbackExecutor) {
        Chat chat = message.getChat();
        sendGreeting(chat, feedbackExecutor);
        bobaFettUserService.createIfNotExists(chat.getId(), () -> BobaFettUser.create(chat));

        return false;
    }

    public void sendGreeting(Chat chat, FeedbackExecutor feedbackExecutor) {
        String name = chat.isUserChat() ? chat.getFirstName() : chat.getTitle();
        feedbackExecutor.sendMessage(
                chat.getId(),
                "Привет, " + name + "! Чтобы узнать что я могу, используй команду "
                        + Commands.COMMANDS + " или меню слева от поля ввода"
        );
    }
}

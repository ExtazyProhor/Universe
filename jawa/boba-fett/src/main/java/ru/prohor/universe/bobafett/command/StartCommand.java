package ru.prohor.universe.bobafett.command;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.service.BobaFettUserService;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
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
        long chatId = chat.getId();
        String name = chat.isUserChat() ? chat.getFirstName() : chat.getTitle();

        bobaFettUserService.createIfNotExists(
                chatId,
                () -> {
                    Opt<String> link = Opt.when(
                            chat.isUserChat() && chat.getUserName() != null,
                            () -> "@" + chat.getUserName()
                    );
                    return new BobaFettUser(
                            ObjectId.get(),
                            chatId,
                            chat.getType(),
                            Opt.ofNullable(name),
                            link,
                            Opt.empty(),
                            Opt.empty()
                    );
                }
        );
        feedbackExecutor.sendMessage(
                chatId,
                "Привет, " + name + "! Чтобы узнать что я могу, используй команду "
                        + Commands.COMMANDS + " или меню слева от поля ввода"
        );
        return false;
    }
}

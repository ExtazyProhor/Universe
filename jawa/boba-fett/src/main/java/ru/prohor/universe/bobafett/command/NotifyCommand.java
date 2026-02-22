package ru.prohor.universe.bobafett.command;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.bobafett.data.pojo.UserStatus;
import ru.prohor.universe.bobafett.service.BobaFettUserService;
import ru.prohor.universe.bobafett.status.WaitNotifyMessage;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NotifyCommand implements CommandHandler {
    private final Set<Long> adminChatsIds;
    private final BobaFettUserService bobaFettUserService;
    private final WaitNotifyMessage waitNotifyMessage;

    public NotifyCommand(
            @Value("${universe.boba-fett.admin-ids}") List<Long> adminChatsIds,
            BobaFettUserService bobaFettUserService,
            WaitNotifyMessage waitNotifyMessage
    ) {
        this.adminChatsIds = new HashSet<>(adminChatsIds);
        this.bobaFettUserService = bobaFettUserService;
        this.waitNotifyMessage = waitNotifyMessage;
    }

    @Override
    public String command() {
        return "/notify";
    }

    @Override
    public void handle(Message message, FeedbackExecutor feedbackExecutor) {
        long chatId = message.getChatId();
        if (!adminChatsIds.contains(chatId))
            return;

        bobaFettUserService.setStatus(chatId, new UserStatus(waitNotifyMessage.key(), Opt.empty()));
        feedbackExecutor.sendMessage(chatId, "На первой строчке chatId через запятую, все остальное - сообщение");
    }
}

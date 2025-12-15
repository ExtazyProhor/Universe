package ru.prohor.universe.bobafett.command;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.bobafett.data.BobaFettRepositoryHelper;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.UserStatus;
import ru.prohor.universe.bobafett.status.WaitNotifyMessage;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.MongoTransactionService;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NotifyCommand implements CommandHandler {
    private final Set<Long> adminChatsIds;
    private final MongoRepository<BobaFettUser> usersRepository;
    private final MongoTransactionService transactionService;
    private final WaitNotifyMessage waitNotifyMessage;

    public NotifyCommand(
            @Value("${universe.boba-fett.admin-ids}") List<Long> adminChatsIds,
            MongoRepository<BobaFettUser> usersRepository,
            MongoTransactionService transactionService,
            WaitNotifyMessage waitNotifyMessage
    ) {
        this.adminChatsIds = new HashSet<>(adminChatsIds);
        this.usersRepository = usersRepository;
        this.transactionService = transactionService;
        this.waitNotifyMessage = waitNotifyMessage;
    }

    @Override
    public String command() {
        return "/notify";
    }

    @Override
    public boolean handle(Message message, FeedbackExecutor feedbackExecutor) {
        Chat chat = message.getChat();
        long chatId = chat.getId();
        if (!adminChatsIds.contains(chatId))
            return false;

        transactionService.withTransaction(tx -> {
            MongoRepository<BobaFettUser> transactional = tx.wrap(usersRepository);
            Opt<BobaFettUser> user = BobaFettRepositoryHelper.findByChatId(transactional, chatId);
            if (user.isEmpty()) {
                // TODO log err unexpected count of users with chatId = $chatId
                return;
            }
            transactional.save(user.get().toBuilder().status(Opt.of(new UserStatus(
                    waitNotifyMessage.key(),
                    Opt.empty()
            ))).build());
            feedbackExecutor.sendMessage(chatId, "На первой строчке chatId через запятую, все остальное - сообщение");
        });
        return false;
    }
}

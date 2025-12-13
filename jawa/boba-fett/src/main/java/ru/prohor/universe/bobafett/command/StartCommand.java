package ru.prohor.universe.bobafett.command;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.bobafett.data.BobaFettRepositoryHelper;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.MongoTransactionService;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler;

@Service
public class StartCommand implements CommandHandler {
    private final String adminLink;
    private final MongoRepository<BobaFettUser> usersRepository;
    private final MongoTransactionService transactionService;

    public StartCommand(
            @Value("${universe.boba-fett.admin-link}") String adminLink,
            MongoRepository<BobaFettUser> usersRepository,
            MongoTransactionService transactionService
    ) {
        this.adminLink = adminLink;
        this.usersRepository = usersRepository;
        this.transactionService = transactionService;
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

        boolean success = transactionService.withTransaction(tx -> {
            MongoRepository<BobaFettUser> transactional = tx.wrap(usersRepository);

            if (!BobaFettRepositoryHelper.containsByChatId(transactional, chatId)) {
                Opt<String> link = Opt.when(
                        chat.isUserChat() && chat.getUserName() != null,
                        () -> "@" + chat.getUserName()
                );
                transactional.save(new BobaFettUser(
                        ObjectId.get(),
                        chatId,
                        chat.getType(),
                        Opt.ofNullable(name),
                        link,
                        Opt.empty(),
                        Opt.empty()
                ));
            }
        });
        if (success) {
            feedbackExecutor.sendMessage(
                    chatId,
                    "Привет, " +
                            name +
                            "! Чтобы узнать что я могу, используй команду /commands или меню слева от поля ввода"
            );
        } else {
            feedbackExecutor.sendMessage(
                    chatId,
                    "Произошла ошибка. Пожалуйста, обратитесь к " + adminLink
            );
        }
        return false;
    }
}

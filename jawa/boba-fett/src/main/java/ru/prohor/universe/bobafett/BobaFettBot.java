package ru.prohor.universe.bobafett;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.bobafett.command.StartCommand;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.service.BobaFettUserService;
import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;
import ru.prohor.universe.jocasta.tgbots.BotSettings;
import ru.prohor.universe.jocasta.tgbots.SimpleBot;

public class BobaFettBot extends SimpleBot {
    private final BobaFettUserService bobaFettUserService;
    private final ObjectMapper objectMapper;
    private final StartCommand startCommand;

    public BobaFettBot(
            BobaFettUserService bobaFettUserService,
            ObjectMapper objectMapper,
            BotSettings settings,
            StartCommand startCommand
    ) {
        super(settings);
        this.bobaFettUserService = bobaFettUserService;
        this.objectMapper = objectMapper;
        this.startCommand = startCommand;
    }

    @Override
    public void onHandlingException(Exception e) {
        // TODO
        Sneaky.throwUnchecked(e);
    }

    @Override
    public void onSendingException(Exception e, long chatId) {
        // TODO
        Sneaky.throwUnchecked(e);
    }

    @Override
    public void onMessage(Message message) {
        // TODO
        Sneaky.execute(() -> System.out.println("unknown message: " + objectMapper.writeValueAsString(message)));
    }

    @Override
    public void onCallback(CallbackQuery callbackQuery) {
        // TODO
        Sneaky.execute(() -> System.out.println("unknown callback: " + objectMapper.writeValueAsString(callbackQuery)));
    }

    @Override
    public void onBotAddedToChat(long chatId, Chat chat) {
        bobaFettUserService.createIfNotExists(chatId, () -> BobaFettUser.create(chat));
        startCommand.sendGreeting(chat, feedbackExecutor);
    }

    @Override
    public void onBotRemovedFromChat(long chatId, Chat chat) {
        bobaFettUserService.disableHolidaysSubscription(chatId);
        // TODO log
    }

    @Override
    public void onUnrecognizedChatMember(long chatId, ChatMemberUpdated chatMemberUpdated) {
        // TODO
        Sneaky.execute(
                () -> System.out.println("my chat member: " + objectMapper.writeValueAsString(chatMemberUpdated))
        );
    }

    @Override
    public void onUnknownAction(Update update) {
        // TODO
        Sneaky.execute(() -> System.out.println("unknown action: " + objectMapper.writeValueAsString(update)));
    }

    @Override
    public void onForbidden(String response, long chatId) {
        bobaFettUserService.disableHolidaysSubscription(chatId);
        // TODO log "$response for $chatId"
    }

    @Override
    public void onMigrateToSuperGroup(long oldChatId, long newChatId) {
        bobaFettUserService.changeChatId(oldChatId, newChatId);
        // TODO log info
    }
}

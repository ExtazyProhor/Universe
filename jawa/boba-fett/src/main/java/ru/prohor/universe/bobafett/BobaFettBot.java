package ru.prohor.universe.bobafett;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberUpdated;
import ru.prohor.universe.bobafett.command.StartCommand;
import ru.prohor.universe.bobafett.service.BobaFettUserService;
import ru.prohor.universe.bobafett.service.ObjectsEncoder;
import ru.prohor.universe.jocasta.tgbots.BotSettings;
import ru.prohor.universe.jocasta.tgbots.SimpleBot;

public class BobaFettBot extends SimpleBot {
    private static final Logger log = LoggerFactory.getLogger(BobaFettBot.class);

    private final BobaFettUserService bobaFettUserService;
    private final ObjectsEncoder objectsEncoder;
    private final StartCommand startCommand;

    public BobaFettBot(
            BobaFettUserService bobaFettUserService,
            ObjectsEncoder objectsEncoder,
            BotSettings settings,
            StartCommand startCommand
    ) {
        super(settings);
        this.bobaFettUserService = bobaFettUserService;
        this.objectsEncoder = objectsEncoder;
        this.startCommand = startCommand;
    }

    @Override
    public void onHandlingException(Exception e) {
        log.error("handling exception", e);
    }

    @Override
    public void onSendingException(Exception e, long chatId) {
        log.error("sending exception", e);
    }

    @Override
    public void onBotAddedToChat(long chatId, Chat chat) {
        bobaFettUserService.createIfNotExists(chat);
        startCommand.sendGreeting(chat, feedbackExecutor);
    }

    @Override
    public void onBotRemovedFromChat(long chatId, Chat chat) {
        bobaFettUserService.safeUpdate(chatId, user -> user.toBuilder().enabled(false).build());
        log.info("bot was removed from chat: '{}', chat-user was deleted", objectsEncoder.encode(chat));
    }

    @Override
    public void onUnrecognizedChatMember(long chatId, ChatMemberUpdated chatMemberUpdated) {
        log.warn("unrecognized chat member: '{}'", objectsEncoder.encode(chatMemberUpdated));
    }

    @Override // TODO убрать, сыпит warn-ами при отправках стикеров
    public void onUnknownAction(Update update) {
        log.warn("unknown action: '{}'", objectsEncoder.encode(update));
    }

    @Override
    public void onForbidden(String response, long chatId) {
        bobaFettUserService.safeUpdate(chatId, user -> user.toBuilder().enabled(false).build());
        log.debug("forbidden for chat with id {}, response - '{}', user was deleted", chatId, response);
    }

    @Override
    public void onMigrateToSuperGroup(long oldChatId, long newChatId) {
        bobaFettUserService.changeChatId(oldChatId, newChatId);
        log.info("chat with id {} was migrated to supergroup with chatId {}", oldChatId, newChatId);
    }
}

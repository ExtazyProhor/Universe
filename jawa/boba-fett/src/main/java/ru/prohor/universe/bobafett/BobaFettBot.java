package ru.prohor.universe.bobafett;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.jocasta.tgbots.BotSettings;
import ru.prohor.universe.jocasta.tgbots.SimpleBot;

public class BobaFettBot extends SimpleBot {
    public BobaFettBot(BotSettings settings) {
        super(settings);
    }

    @Override
    public void onHandlingException(Exception e) {

    }

    @Override
    public void onSendingException(Exception e, String chatId) {

    }

    @Override
    public void onMessage(Message message) {

    }

    @Override
    public void onCallback(CallbackQuery callbackQuery) {

    }

    @Override
    public void onMyChatMember(ChatMemberUpdated chatMemberUpdated) {

    }

    @Override
    public void onUnknownAction(Update update) {

    }
}

package ru.prohor.universe.bobafett.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.bobafett.service.BackupService;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler;

@Service
public class BackupCommand implements CommandHandler {
    private final BackupService backupService;

    public BackupCommand(BackupService backupService) {
        this.backupService = backupService;
    }

    @Override
    public String command() {
        return "/backup";
    }

    @Override
    public void handle(Message message, FeedbackExecutor feedbackExecutor) {
        long chatId = message.getChatId();
        if (!backupService.isAdmin(chatId))
            return;

        SendMessage sendMessage = SendMessage.builder()
                .chatId(message.getChatId())
                .text("```json\n" + backupService.createBackupJson() + "\n```")
                .parseMode(ParseMode.MARKDOWNV2)
                .build();
        feedbackExecutor.sendMessage(sendMessage);
    }
}

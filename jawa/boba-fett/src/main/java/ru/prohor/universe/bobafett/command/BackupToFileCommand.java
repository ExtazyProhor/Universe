package ru.prohor.universe.bobafett.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.bobafett.service.BackupService;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler;

@Service
public class BackupToFileCommand implements CommandHandler {
    private final BackupService backupService;

    public BackupToFileCommand(BackupService backupService) {
        this.backupService = backupService;
    }

    @Override
    public String command() {
        return "/backup_to_file";
    }

    @Override
    public void handle(Message message, FeedbackExecutor feedbackExecutor) {
        long chatId = message.getChatId();
        if (!backupService.isAdmin(chatId))
            return;

        feedbackExecutor.sendDocument(
                chatId,
                backupService.createBackupJson(),
                "boba-fett-backup.json"
        );
    }
}

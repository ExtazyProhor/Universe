package ru.prohor.universe.bobafett.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.bobafett.service.BackupService;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
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

        Opt<String> json = backupService.createBackupJson();
        if (json.isPresent()) {
            feedbackExecutor.sendDocument(
                    chatId,
                    json.get(),
                    "boba-fett-backup.json"
            );
        } else {
            feedbackExecutor.sendMessage(
                    chatId,
                    "Произошла ошибка при создании копии"
            );
        }
    }
}

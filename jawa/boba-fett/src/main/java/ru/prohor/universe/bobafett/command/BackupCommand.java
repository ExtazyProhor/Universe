package ru.prohor.universe.bobafett.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.morphia.MongoForceBackupService;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class BackupCommand implements CommandHandler {
    private final Set<Long> adminChatsIds;
    private final MongoForceBackupService mongoForceBackupService;
    private final ObjectWriter writer;

    public BackupCommand(
            @Value("${universe.boba-fett.admin-ids}") List<Long> adminChatsIds,
            MongoForceBackupService mongoForceBackupService,
            ObjectMapper objectMapper
    ) {
        this.adminChatsIds = new HashSet<>(adminChatsIds);
        this.mongoForceBackupService = mongoForceBackupService;
        this.writer = objectMapper.writerWithDefaultPrettyPrinter();
    }

    @Override
    public String command() {
        return "/backup";
    }

    @Override
    public boolean handle(Message message, FeedbackExecutor feedbackExecutor) {
        long chatId = message.getChatId();
        if (!adminChatsIds.contains(chatId))
            return false;

        Map<String, List<?>> backup = mongoForceBackupService.backup();
        Opt<String> json = Opt.tryOrNull(() -> writer.writeValueAsString(backup));
        feedbackExecutor.sendMessage(message.getChatId(), json.orElse("Произошла ошибка при создании копии"));
        return false;
    }
}

package ru.prohor.universe.bobafett.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.morphia.MongoForceBackupService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class BackupService {
    private final Set<Long> adminChatsIds;
    private final MongoForceBackupService mongoForceBackupService;
    private final ObjectWriter writer;

    public BackupService(
            @Value("${universe.boba-fett.admin-ids}") List<Long> adminChatsIds,
            MongoForceBackupService mongoForceBackupService,
            ObjectMapper objectMapper
    ) {
        this.adminChatsIds = new HashSet<>(adminChatsIds);
        this.mongoForceBackupService = mongoForceBackupService;
        this.writer = objectMapper.writerWithDefaultPrettyPrinter();
    }

    public boolean isAdmin(long chatId) {
        return adminChatsIds.contains(chatId);
    }

    public Opt<String> createBackupJson() {
        Map<String, List<?>> backup = mongoForceBackupService.backup();
        return Opt.tryOrNull(() -> writer.writeValueAsString(backup));
    }
}

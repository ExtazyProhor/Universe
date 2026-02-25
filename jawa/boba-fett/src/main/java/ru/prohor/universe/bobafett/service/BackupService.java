package ru.prohor.universe.bobafett.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.jackson.morphia.MongoForceBackupService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BackupService {
    private final Set<Long> adminChatsIds;
    private final MongoForceBackupService mongoForceBackupService;

    public BackupService(
            @Value("${universe.boba-fett.admin-ids}") List<Long> adminChatsIds,
            MongoForceBackupService mongoForceBackupService
    ) {
        this.adminChatsIds = new HashSet<>(adminChatsIds);
        this.mongoForceBackupService = mongoForceBackupService;
    }

    public boolean isAdmin(long chatId) {
        return adminChatsIds.contains(chatId);
    }

    public String createBackupJson() {
        return mongoForceBackupService.backupAsPrettyJson();
    }
}

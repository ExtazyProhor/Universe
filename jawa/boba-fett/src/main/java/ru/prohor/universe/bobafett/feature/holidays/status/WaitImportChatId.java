package ru.prohor.universe.bobafett.feature.holidays.status;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.bobafett.command.Commands;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.CustomHoliday;
import ru.prohor.universe.bobafett.data.pojo.UserStatus;
import ru.prohor.universe.bobafett.feature.holidays.CustomHolidaysService;
import ru.prohor.universe.bobafett.service.BobaFettUserService;
import ru.prohor.universe.bobafett.status.Statuses;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.collections.tuple.Tuple3;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.MongoTransaction;
import ru.prohor.universe.jocasta.morphia.MongoTransactionService;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusHandler;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WaitImportChatId implements StatusHandler<String> {
    private static final String ILLEGAL_ID_FORMAT = """
            Неправильный формат ID, должно быть целое число.
            Напишите ID заново или отмените импорт праздников с помощью команды /cancel""";

    private final BobaFettUserService bobaFettUserService;
    private final CustomHolidaysService customHolidaysService;
    private final MongoRepository<BobaFettUser> bobaFettUsersRepository;
    private final MongoRepository<CustomHoliday> customHolidaysRepository;
    private final MongoTransactionService mongoTransactionService;

    public WaitImportChatId(
            BobaFettUserService bobaFettUserService,
            CustomHolidaysService customHolidaysService,
            MongoRepository<BobaFettUser> bobaFettUsersRepository,
            MongoRepository<CustomHoliday> customHolidaysRepository,
            MongoTransactionService mongoTransactionService
    ) {
        this.bobaFettUserService = bobaFettUserService;
        this.customHolidaysService = customHolidaysService;
        this.bobaFettUsersRepository = bobaFettUsersRepository;
        this.customHolidaysRepository = customHolidaysRepository;
        this.mongoTransactionService = mongoTransactionService;
    }

    @Override
    public String key() {
        return Statuses.WAIT_IMPORT_CHAT_ID;
    }

    @Override
    public boolean handle(Update update, FeedbackExecutor feedbackExecutor) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return true;
        }
        long chatId = update.getMessage().getChatId();
        String importChatId = update.getMessage().getText();

        if (importChatId.equals(Commands.CANCEL)) {
            feedbackExecutor.sendMessage(chatId, "Импорт праздников отменен");
            return false;
        }

        long importChatIdLong;
        try {
            importChatIdLong = Long.parseLong(importChatId);
        } catch (NumberFormatException e) {
            bobaFettUserService.setStatus(chatId, new UserStatus(key(), Opt.empty()));
            feedbackExecutor.sendMessage(chatId, ILLEGAL_ID_FORMAT);
            return false;
        }

        mongoTransactionService.withTransaction(tx -> {
            MongoRepository<BobaFettUser> transactional = tx.wrap(bobaFettUsersRepository);
            if (!bobaFettUserService.contains(transactional, importChatIdLong)) {
                feedbackExecutor.sendMessage(chatId, "Пользователь с таким ID не найден");
                return;
            }
            feedbackExecutor.sendMessage(chatId, importHolidays(tx, importChatIdLong, chatId));
        });
        return false;
    }

    private String importHolidays(MongoTransaction tx, long sourceChatId, long chatId) {
        MongoRepository<CustomHoliday> transactional = tx.wrap(customHolidaysRepository);
        List<CustomHoliday> holidaysToImport = customHolidaysService.findCustomHolidays(transactional, sourceChatId);
        if (holidaysToImport.isEmpty()) {
            return "У пользователя с указанным ID нет собственных праздников";
        }
        List<CustomHoliday> existing = customHolidaysService.findCustomHolidays(transactional, chatId);
        Set<Tuple3<String, Integer, Integer>> existingHolidaysKeys = existing.stream()
                .map(this::toTuple)
                .collect(Collectors.toSet());
        int expectedCount = holidaysToImport.size();
        List<CustomHoliday> newHolidays = holidaysToImport.stream()
                .filter(holiday -> !existingHolidaysKeys.contains(toTuple(holiday)))
                .map(holiday -> copy(holiday, chatId))
                .toList();
        int addedCount = newHolidays.size();
        transactional.save(newHolidays);

        return "Импортировано праздников: " + addedCount + "\n" +
                "Пропущено дубликатов: " + (expectedCount - addedCount);
    }

    private Tuple3<String, Integer, Integer> toTuple(CustomHoliday holiday) {
        return new Tuple3<>(holiday.holidayName(), holiday.month(), holiday.dayOfMonth());
    }

    private CustomHoliday copy(CustomHoliday holiday, long chatId) {
        return new CustomHoliday(
                ObjectId.get(),
                chatId,
                holiday.month(),
                holiday.dayOfMonth(),
                holiday.holidayName()
        );
    }
}

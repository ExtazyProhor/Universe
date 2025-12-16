package ru.prohor.universe.bobafett.feature.holidays.status;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.bobafett.command.CancelCommandHandler;
import ru.prohor.universe.bobafett.data.BobaFettRepositoryHelper;
import ru.prohor.universe.bobafett.data.MongoStatusStorage;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.CustomHoliday;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.collections.tuple.Tuple3;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.MongoTransactionService;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusHandler;
import ru.prohor.universe.jocasta.tgbots.api.status.ValuedStatusStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WaitImportChatId implements StatusHandler<String> {
    private static final String ILLEGAL_ID_FORMAT = """
            Неправильный формат ID, должно быть целое число.
            Напишите ID заново или отмените импорт праздников с помощью команды /cancel""";

    private final CancelCommandHandler cancelCommandHandler;
    private final MongoStatusStorage mongoStatusStorage;
    private final MongoRepository<BobaFettUser> bobaFettUsersRepository;
    private final MongoRepository<CustomHoliday> customHolidaysRepository;
    private final MongoTransactionService mongoTransactionService;

    public WaitImportChatId(
            CancelCommandHandler cancelCommandHandler,
            MongoStatusStorage mongoStatusStorage,
            MongoRepository<BobaFettUser> bobaFettUsersRepository,
            MongoRepository<CustomHoliday> customHolidaysRepository,
            MongoTransactionService mongoTransactionService
    ) {
        this.cancelCommandHandler = cancelCommandHandler;
        this.mongoStatusStorage = mongoStatusStorage;
        this.bobaFettUsersRepository = bobaFettUsersRepository;
        this.customHolidaysRepository = customHolidaysRepository;
        this.mongoTransactionService = mongoTransactionService;
    }

    @Override
    public String key() {
        return "holidays/wait-import-id";
    }

    @Override
    public boolean handle(Update update, FeedbackExecutor feedbackExecutor) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return true;
        }
        long chatId = update.getMessage().getChatId();
        String importChatId = update.getMessage().getText();

        if (importChatId.equals(cancelCommandHandler.command())) {
            feedbackExecutor.sendMessage(chatId, "Импорт праздников отменен");
            return false;
        }

        long importChatIdLong;
        try {
            importChatIdLong = Long.parseLong(importChatId);
        } catch (NumberFormatException e) {
            boolean success = mongoStatusStorage.setStatus(
                    chatId,
                    new ValuedStatusStorage.ValuedStatus<>(key(), Opt.empty())
            );
            if (!success) {
                // TODO log err saving status
            }
            feedbackExecutor.sendMessage(chatId, ILLEGAL_ID_FORMAT);
            return false;
        }

        if (!BobaFettRepositoryHelper.containsByChatId(bobaFettUsersRepository, importChatIdLong)) {
            feedbackExecutor.sendMessage(chatId, "Пользователь с таким ID не найден");
            return false;
        }

        feedbackExecutor.sendMessage(chatId, importHolidays(importChatIdLong, chatId));
        return false;
    }

    private String importHolidays(long sourceChatId, long chatId) {
        return mongoTransactionService.withTransaction(tx -> {
            MongoRepository<CustomHoliday> transactional = tx.wrap(customHolidaysRepository);
            List<CustomHoliday> holidaysToImport = BobaFettRepositoryHelper.findCustomHolidaysByChatId(
                    transactional,
                    sourceChatId
            );
            if (holidaysToImport.isEmpty()) {
                return "У пользователя с указанным ID нет собственных праздников";
            }
            List<CustomHoliday> existing = BobaFettRepositoryHelper.findCustomHolidaysByChatId(
                    transactional,
                    chatId
            );
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
        }).asOpt().orElse("Произошла ошибка, попробуйте позже");
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

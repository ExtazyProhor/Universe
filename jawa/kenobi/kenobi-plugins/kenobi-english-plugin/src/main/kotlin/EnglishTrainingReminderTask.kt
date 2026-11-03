package ru.prohor.universe.kenobi.plugin.english

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.prohor.universe.jocasta.core.features.fieldref.KFR
import ru.prohor.universe.jocasta.core.string.MarkdownV2
import ru.prohor.universe.jocasta.morphia.MongoRepository
import ru.prohor.universe.jocasta.morphia.filter.MongoFilter
import ru.prohor.universe.jocasta.morphia.filter.MongoFilters
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor
import ru.prohor.universe.kenobi.core.KenobiUser
import ru.prohor.universe.kenobi.plugin.english.model.WordsSet

@Service
class EnglishTrainingReminderTask(
    private val usersRepository: MongoRepository<KenobiUser>,
    private val wordsRepository: MongoRepository<WordsSet>,
    private val feedbackExecutor: FeedbackExecutor,
    @Value($$"${universe.kenobi.plugins.english.training-url}")
    trainingUrl: String,
) {
    private val reminderText = MarkdownV2()
        .bold("Пришло время выучить новые английские слова!")
        .newline(2)
        .link("Начать тренировку", trainingUrl)

    @Scheduled(cron = "0 0 7,15 * * *", zone = "UTC")
    fun sendReminders() {
        usersRepository.find(FILTER)
            .filter { wordsRepository.findById(it.id).isPresent }
            .forEach { user ->
                user.telegramChatId?.let { chatId ->
                    feedbackExecutor.sendMarkdownV2Message(chatId, reminderText)
                }
            }
    }

    companion object {
        val FILTER: MongoFilter<KenobiUser> = MongoFilters.and(
            MongoFilters.exists(KFR.wrapO(KenobiUser::telegramChatId)),
            MongoFilters.ne(KFR.wrapO(KenobiUser::telegramChatId), null)
        )
    }
}

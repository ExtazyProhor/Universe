package ru.prohor.universe.kenobi.plugin.english

import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import ru.prohor.universe.jocasta.morphia.MongoRepository
import ru.prohor.universe.kenobi.plugin.english.model.TrainingBatch
import ru.prohor.universe.kenobi.plugin.english.model.Word
import ru.prohor.universe.kenobi.plugin.english.model.WordReview
import ru.prohor.universe.kenobi.plugin.english.model.WordsSet
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

@Service
class EnglishTrainingService(
    private val repository: MongoRepository<WordsSet>
) {
    fun start(
        userId: ObjectId,
        now: Long = System.currentTimeMillis()
    ): Pair<TrainingBatch?, List<Word>> {
        // TODO mongo transaction
        val set = repository.findById(userId).orElseNull() ?: return null to emptyList()
        val words = set.words
        if (words.isEmpty()) return null to emptyList()

        val byForm = words.associateBy { it.englishForm }
        // проверяем что все слова из незаконченной тренировки остались в словаре
        val active = set.training?.takeIf { batch -> batch.englishForms.all { it in byForm } }
        if (active != null) return active to active.englishForms.map { byForm.getValue(it) }

        // слова, которые уже пора повторять
        val due = words.filter { (set.reviews[it.englishForm]?.nextReviewAt ?: Long.MAX_VALUE) <= now }
            .shuffled().sortedBy { set.reviews.getValue(it.englishForm).nextReviewAt }.take(WORDS_COUNT)
        // слова, которые никогда не участвовали в тренировках
        val fresh = words.filter { it.englishForm !in set.reviews }.shuffled().take(WORDS_COUNT)
        // уже изучавшиеся слова, которые пока не пора повторять
        val future = words.filter { (set.reviews[it.englishForm]?.nextReviewAt ?: 0) > now }
            .shuffled().sortedBy { set.reviews.getValue(it.englishForm).nextReviewAt }.take(WORDS_COUNT)

        // при наличии накопившихся повторений резервируем часть подхода (3 места) под новые слова
        val chosen = (due.take(if (fresh.isEmpty()) WORDS_COUNT else DUE_WORDS_COUNT)
                + fresh.take(FRESH_WORDS_COUNT)).toMutableList()
        val chosenForms = chosen.map { it.englishForm }.toSet()
        chosen += (due + fresh + future).filter { it.englishForm !in chosenForms }.take(WORDS_COUNT - chosen.size)

        val batch = TrainingBatch(
            id = ObjectId().toString(),
            englishForms = chosen.shuffled().map { it.englishForm }
        )
        repository.save(set.copy(training = batch))
        return batch to batch.englishForms.map { byForm.getValue(it) }
    }

    fun complete(
        userId: ObjectId,
        request: CompleteTrainingRequest,
        now: Long = System.currentTimeMillis()
    ) {
        // TODO mongo transaction
        val set = repository.findById(userId).orElseNull() ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        if (set.lastCompletedTrainingId == request.id) return
        val batch = set.training?.takeIf { it.id == request.id }
            ?: throw ResponseStatusException(HttpStatus.CONFLICT, "Training is no longer active")
        val forms = request.results.map { it.englishForm }
        if (forms.size != batch.englishForms.size || forms.toSet() != batch.englishForms.toSet()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Results must match all issued cards exactly once")
        }

        val present = set.words.map { it.englishForm }.toSet()
        val reviews = set.reviews.filterKeys { it in present }.toMutableMap()
        request.results.filter { it.englishForm in present }.forEach { result ->
            val old = reviews[result.englishForm]
            val due = old == null || old.nextReviewAt <= now
            // вычисление ступени повторения, принимает значения [1; STAGES]
            val stage = when {
                result.difficult -> 1
                !due -> old.stage
                else -> ((old?.stage ?: 0) + 1).coerceAtMost(STAGES)
            }
            val next = if (!result.difficult && !due) {
                old.nextReviewAt
            } else {
                now + REPEAT_DAYS_PERIODS[stage.coerceIn(1, STAGES) - 1].inWholeMilliseconds
            }
            reviews[result.englishForm] = WordReview(stage, next, now, (old?.trainingCount ?: 0) + 1)
        }
        repository.save(set.copy(reviews = reviews, training = null, lastCompletedTrainingId = batch.id))
    }

    companion object {
        private const val WORDS_COUNT = 10
        private const val FRESH_WORDS_COUNT = 2
        private const val DUE_WORDS_COUNT = WORDS_COUNT - FRESH_WORDS_COUNT

        private val REPEAT_DAYS_PERIODS = listOf(
            5.hours,
            1.days,
            3.days,
            7.days,
            14.days,
            30.days,
            90.days,
            180.days,
            365.days,
        )
        private val STAGES = REPEAT_DAYS_PERIODS.size
    }
}

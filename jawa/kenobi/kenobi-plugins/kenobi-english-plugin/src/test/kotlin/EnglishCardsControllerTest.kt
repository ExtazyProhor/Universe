package ru.prohor.universe.kenobi.plugin.english

import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.web.server.ResponseStatusException
import ru.prohor.universe.jocasta.core.collections.common.Opt
import ru.prohor.universe.jocasta.morphia.impl.MongoInMemoryRepository
import ru.prohor.universe.kenobi.plugin.english.model.Word
import ru.prohor.universe.kenobi.plugin.english.model.WordReview
import ru.prohor.universe.kenobi.plugin.english.model.WordsSet
import ru.prohor.universe.kenobi.plugin.english.translation.AIRequestInfo
import ru.prohor.universe.kenobi.plugin.english.translation.TranslationService
import ru.prohor.universe.kenobi.plugin.english.translation.WordTranslation
import ru.prohor.universe.kenobi.plugin.english.translation.WordTranslationItem
import ru.prohor.universe.scarif.jwt.AuthorizedUser
import java.util.UUID

class EnglishCardsControllerTest {
    private val repository = MongoInMemoryRepository({ it.id }, WordsSet::class.java)
    private val service = EnglishTrainingService(repository)
    private val translations = mock(TranslationService::class.java)
    private val controller = EnglishCardsController(service, translations)
    private val userId = ObjectId()
    private val user = Opt.of(AuthorizedUser(1, UUID.randomUUID(), userId.toHexString()))
    private val words = (1..40).map { Word("word $it", listOf("перевод $it")) }
    private val day = 86_400_000L

    @Test
    fun `old collections load with no review state and new progress survives serialization`() {
        val mapper = com.fasterxml.jackson.databind.ObjectMapper()
            .registerModule(com.fasterxml.jackson.module.kotlin.kotlinModule())
            .registerModule(ru.prohor.universe.jocasta.morphia.jackson.JacksonMorphiaConfiguration.createMorphiaModule())
        val original = WordsSet(userId, words.take(1))
        val legacyJson = mapper.valueToTree<com.fasterxml.jackson.databind.node.ObjectNode>(original)
        legacyJson.remove(listOf("reviews", "training", "lastCompletedTrainingId"))
        assertEquals(original, mapper.treeToValue(legacyJson, WordsSet::class.java))
        repository.save(original)
        finish(100)
        val saved = repository.ensuredFindById(userId)
        assertEquals(saved, mapper.readValue(mapper.writeValueAsString(saved), WordsSet::class.java))
    }

    @Test
    fun `auth is required and only authenticated user's words are issued`() {
        repository.save(WordsSet(userId, words))
        repository.save(WordsSet(ObjectId(), listOf(Word("private", listOf("чужое")))))
        assertEquals(
            401,
            assertThrows(ResponseStatusException::class.java) { controller.start(Opt.empty()) }.statusCode.value()
        )
        val response = controller.start(user)
        assertEquals(20, response.body!!.cards.size)
        assertTrue(response.body!!.cards.all { it.englishForm.startsWith("word ") })
        assertEquals("no-store", response.headers.cacheControl)
        verify(translations, times(20)).getTranslation(anyString())
    }

    @Test
    fun `enriched meanings are exposed without AI request metadata and null falls back`() {
        repository.save(WordsSet(userId, words.take(2)))
        val groups = listOf(WordTranslationItem("смысл", "An example.", "Пример."))
        `when`(translations.getTranslation("word 1")).thenReturn(
            WordTranslation(
                ObjectId(),
                "word 1",
                groups,
                AIRequestInfo("model", "low", null, null, null, null, null)
            )
        )
        val cards = controller.start(user).body!!.cards.associateBy { it.englishForm }
        assertEquals(groups, cards.getValue("word 1").translations)
        assertEquals(emptyList<WordTranslationItem>(), cards.getValue("word 2").translations)
        assertEquals(listOf("перевод 2"), cards.getValue("word 2").translates)
    }

    @Test
    fun `due backlog includes new words and is capped at twenty`() {
        val reviews = words.take(25).associate { it.englishForm to WordReview(3, 10, 0, 2) }
        repository.save(WordsSet(userId, words, reviews))
        val (batch, selected) = service.start(userId, 100)
        assertEquals(20, selected.size)
        assertEquals(3, selected.count { it.englishForm !in reviews })
        assertEquals(batch, service.start(userId, 100).first)
        assertEquals(reviews, repository.ensuredFindById(userId).reviews)
    }

    private fun finish(now: Long, difficult: Boolean = false): String {
        val (batch, selected) = service.start(userId, now)
        service.complete(
            userId,
            CompleteTrainingRequest(batch!!.id, selected.map { TrainingResult(it.englishForm, difficult) }),
            now
        )
        return batch.id
    }

    @Test
    fun `first training then spaced reviews advance intervals while early practice does not`() {
        repository.save(WordsSet(userId, words.take(1)))
        val firstId = finish(100)
        val first = repository.ensuredFindById(userId).reviews.getValue("word 1")
        assertEquals(1, first.stage)
        assertEquals(100 + day, first.nextReviewAt)
        service.complete(userId, CompleteTrainingRequest(firstId, emptyList()), 200)
        assertEquals(first, repository.ensuredFindById(userId).reviews.getValue("word 1"))
        finish(200)
        assertEquals(1, repository.ensuredFindById(userId).reviews.getValue("word 1").stage)
        finish(first.nextReviewAt)
        val second = repository.ensuredFindById(userId).reviews.getValue("word 1")
        assertEquals(2, second.stage)
        assertEquals(first.nextReviewAt + 3 * day, second.nextReviewAt)
        assertEquals(3, second.trainingCount)
        finish(second.nextReviewAt, true)
        val reset = repository.ensuredFindById(userId).reviews.getValue("word 1")
        assertEquals(1, reset.stage)
        assertEquals(second.nextReviewAt + day, reset.nextReviewAt)
    }

    @Test
    fun `rejects partial duplicate foreign and other-user results without changing progress`() {
        repository.save(WordsSet(userId, words.take(2)))
        val (batch, _) = service.start(userId, 100)
        val other = ObjectId()
        repository.save(WordsSet(other, words.take(2)))
        service.start(other, 100)
        val valid = CompleteTrainingRequest(batch!!.id, words.take(2).map { TrainingResult(it.englishForm, false) })
        assertThrows(ResponseStatusException::class.java) { service.complete(other, valid, 100) }
        for (results in listOf(
            emptyList(),
            listOf(valid.results[0], valid.results[0]),
            listOf(TrainingResult("foreign", false))
        )) {
            assertThrows(ResponseStatusException::class.java) {
                service.complete(
                    userId,
                    valid.copy(results = results),
                    100
                )
            }
        }
        assertTrue(repository.ensuredFindById(userId).reviews.isEmpty())
        service.complete(userId, valid, 100)
        val saved = repository.ensuredFindById(userId)
        // A retry of an older completion must not wipe a newly started training.
        val newer = service.start(userId, 100).first
        service.complete(userId, valid, 100)
        assertEquals(saved.reviews, repository.ensuredFindById(userId).reviews)
        assertEquals(newer, repository.ensuredFindById(userId).training)
    }

    @Test
    fun `empty and changed dictionaries are handled without issuing removed cards`() {
        assertTrue(service.start(userId).second.isEmpty())
        repository.save(WordsSet(userId, words.take(1)))
        val old = service.start(userId).first
        repository.save(repository.ensuredFindById(userId).copy(words = words.drop(1).take(1)))
        val (batch, cards) = service.start(userId)
        assertNotEquals(old!!.id, batch!!.id)
        assertEquals("word 2", cards.single().englishForm)
    }
}

package ru.prohor.universe.kenobi.plugin.english

import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.prohor.universe.jocasta.core.features.fieldref.FR
import ru.prohor.universe.jocasta.morphia.MongoRepository
import ru.prohor.universe.jocasta.morphia.filter.MongoFilters
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor
import ru.prohor.universe.kenobi.plugin.english.model.Word
import ru.prohor.universe.kenobi.plugin.english.model.WordsSet

@RestController
@RequestMapping("/api/english/save-words")
class EnglishWordsController(
    private val feedbackExecutor: FeedbackExecutor,
    private val wordsRepository: MongoRepository<WordsSet>,
    @param:Value($$"${universe.kenobi.plugins.english.preset-1.upload-token}")
    private val uploadTokenPreset1: String,
    @param:Value($$"${universe.kenobi.plugins.english.preset-1.chat-id}")
    private val chatIdPreset1: Long,
) {
    @PostMapping("/preset/1", consumes = [MediaType.TEXT_PLAIN_VALUE])
    fun saveWordsPreset1(
        @RequestHeader("X-Upload-Token") token: String,
        @RequestBody body: String,
    ): ResponseEntity<String> {
        if (token != uploadTokenPreset1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val lines = body.split("\n")
        val words = lines.drop(lines.indexOfFirst { it == "---" } + 1)
            .filter { it.isNotBlank() }
            .onEach {
                if (!it.contains(" - ")) {
                    return ResponseEntity.badRequest().body("Illegal format of line: '$it'")
                }
            }
            .map {
                Word(
                    englishForm = it.substringBefore(" - "),
                    translates = it.substringAfter(" - ").split(", ")
                )
            }

        val updatedWordSet = findByChatId(chatIdPreset1).copy(words = words)
        wordsRepository.save(updatedWordSet)
        feedbackExecutor.sendMessage(chatIdPreset1, "Words file updated: now you have ${words.size} words")

        return ResponseEntity.ok().build()
    }

    private fun findByChatId(chatId: Long): WordsSet {
        return wordsRepository.findOne(MongoFilters.eq(FR.wrap(WordsSet::chatId), chatId))
            .orElseNull() ?: WordsSet(ObjectId(), chatId, emptyList())
    }
}

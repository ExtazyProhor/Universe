package ru.prohor.universe.kenobi.plugin.english

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.prohor.universe.jocasta.core.features.fieldref.KFR
import ru.prohor.universe.jocasta.morphia.MongoRepository
import ru.prohor.universe.jocasta.morphia.filter.MongoFilters
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor
import ru.prohor.universe.kenobi.core.KenobiUser
import ru.prohor.universe.kenobi.plugin.english.model.Word
import ru.prohor.universe.kenobi.plugin.english.model.WordsSet

@RestController
@RequestMapping("/api/english")
class EnglishWordsController(
    private val feedbackExecutor: FeedbackExecutor,
    private val wordsRepository: MongoRepository<WordsSet>,
    private val usersRepository: MongoRepository<KenobiUser>,
) {
    @PostMapping("/save-words/preset/{preset}", consumes = [MediaType.TEXT_PLAIN_VALUE])
    fun saveWords(
        @PathVariable("preset") preset: Int,
        @RequestHeader("X-Upload-Token") token: String,
        @RequestBody body: String,
    ): ResponseEntity<String> {

        val user = findByToken(token) ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        val chatId = user.telegramChatId ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("The user with the specified API token lacks a chatId")

        val words = parseWords(chatId, preset, body)
        val updatedWordSet = wordsRepository.findById(user.id).orElseNull()?.copy(words = words)
            ?: WordsSet(user.id, words)

        wordsRepository.save(updatedWordSet)
        feedbackExecutor.sendMessage(chatId, "Words file updated: now you have ${words.size} words")

        return ResponseEntity.ok().build()
    }

    private fun parseWords(chatId: Long, preset: Int, body: String): List<Word> {
        if (preset != 1) return emptyList()

        val lines = body.split("\n")
        val illegalLines = mutableListOf<String>()
        val words = lines.asSequence().drop(lines.indexOfFirst { it == "---" } + 1)
            .filter { it.isNotBlank() }
            .filter { !it.startsWith("#") }
            .filter {
                val valid = it.contains(" - ")
                if (!valid) illegalLines.add(it)
                valid
            }
            .map {
                Word(
                    englishForm = it.substringBefore(" - "),
                    translates = it.substringAfter(" - ").split(", ")
                )
            }.toList()

        if (illegalLines.isNotEmpty()) {
            val moreMessage = if (illegalLines.size > 10) {
                "\n\nand ${illegalLines.size - 10} more lines"
            } else {
                ""
            }
            feedbackExecutor.sendMessage(
                chatId,
                "Illegal format of some lines:\n\n" + illegalLines.take(10).joinToString("\n") + moreMessage
            )
        }
        return words
    }

    private fun findByToken(token: String): KenobiUser? {
        return usersRepository.findOne(MongoFilters.eq(KFR.wrapO(KenobiUser::apiToken), token)).orElseNull()
    }
}

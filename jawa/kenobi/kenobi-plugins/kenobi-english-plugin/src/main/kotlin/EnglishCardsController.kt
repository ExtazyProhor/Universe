package ru.prohor.universe.kenobi.plugin.english

import org.bson.types.ObjectId
import org.springframework.http.CacheControl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import ru.prohor.universe.jocasta.core.collections.common.Opt
import ru.prohor.universe.kenobi.plugin.english.translation.TranslationService
import ru.prohor.universe.kenobi.plugin.english.translation.WordTranslationItem
import ru.prohor.universe.scarif.jwt.AuthorizedUser

@RestController
@RequestMapping("/api/english/training")
class EnglishCardsController(
    private val trainingService: EnglishTrainingService,
    private val translationService: TranslationService,
) {
    @PostMapping
    fun start(
        @RequestAttribute(name = AuthorizedUser.AUTHORIZED_USER_ATTRIBUTE_KEY)
        user: Opt<AuthorizedUser>,
    ): ResponseEntity<TrainingResponse> {
        val (batch, words) = trainingService.start(userId(user))
        val cards = words.map { word ->
            TrainingCard(
                englishForm = word.englishForm,
                translates = word.translates,
                translations = translationService.getTranslation(word.englishForm)?.translations ?: emptyList()
            )
        }
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(TrainingResponse(batch?.id, cards))
    }

    @PostMapping("/complete")
    fun complete(
        @RequestAttribute(name = AuthorizedUser.AUTHORIZED_USER_ATTRIBUTE_KEY)
        user: Opt<AuthorizedUser>,
        @RequestBody request: CompleteTrainingRequest,
    ): ResponseEntity<Void> {
        trainingService.complete(userId(user), request)
        return ResponseEntity.noContent().build()
    }

    private fun userId(user: Opt<AuthorizedUser>): ObjectId {
        val id = user.orElseNull()?.objectId()
        if (id == null || !ObjectId.isValid(id)) throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        return ObjectId(id)
    }
}

data class TrainingCard(
    val englishForm: String,
    val translates: List<String>,
    val translations: List<WordTranslationItem>
)

data class TrainingResponse(val id: String?, val cards: List<TrainingCard>)
data class TrainingResult(val englishForm: String, val difficult: Boolean)
data class CompleteTrainingRequest(val id: String, val results: List<TrainingResult>)

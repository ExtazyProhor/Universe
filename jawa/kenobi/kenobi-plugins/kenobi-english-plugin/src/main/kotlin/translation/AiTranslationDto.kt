package ru.prohor.universe.kenobi.plugin.english.translation

import com.fasterxml.jackson.annotation.JsonProperty

data class TranslationResponse(
    val source: String,
    val translations: List<TranslationResponseItem>
)

data class TranslationResponseItem(
    val translation: List<String>,
    val example: String,
    @field:JsonProperty("example-translation")
    val exampleTranslation: String
)

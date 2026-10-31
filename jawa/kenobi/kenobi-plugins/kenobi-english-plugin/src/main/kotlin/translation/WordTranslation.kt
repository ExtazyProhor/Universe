package ru.prohor.universe.kenobi.plugin.english.translation

import org.bson.types.ObjectId

data class WordTranslation(
    val id: ObjectId,
    val source: String,
    val translations: List<WordTranslationItem>,
    val aiRequestInfoId: ObjectId
)

data class WordTranslationItem(
    val translation: List<String>,
    val example: String,
    val exampleTranslation: String
)

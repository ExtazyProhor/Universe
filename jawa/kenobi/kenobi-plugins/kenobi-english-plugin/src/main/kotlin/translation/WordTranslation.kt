package ru.prohor.universe.kenobi.plugin.english.translation

import org.bson.types.ObjectId

data class WordTranslation(
    val id: ObjectId,
    val source: String,
    val translations: List<WordTranslationItem>,
    val aiRequestInfo: AIRequestInfo
)

data class WordTranslationItem(
    val translation: String,
    val example: String,
    val exampleTranslation: String
)

data class AIRequestInfo(
    val model: String,
    val thinkingLevel: String,
    val promptTokenCount: Int?,
    val outputTokenCount: Int?,
    val thoughtsTokenCount: Int?,
    val toolUsePromptTokenCount: Int?,
    val totalTokenCount: Int?
)

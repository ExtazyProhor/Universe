package ru.prohor.universe.kenobi.plugin.english.translation

import org.bson.types.ObjectId

data class AIRequestInfo(
    val id: ObjectId,
    val model: String,
    val thinkingLevel: String,
    val promptTokenCount: Int?,
    val outputTokenCount: Int?,
    val thoughtsTokenCount: Int?,
    val toolUsePromptTokenCount: Int?,
    val totalTokenCount: Int?
)

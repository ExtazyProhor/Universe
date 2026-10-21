package ru.prohor.universe.kenobi.plugin.english.model

import org.bson.types.ObjectId

data class WordsSet(
    val id: ObjectId,
    val chatId: Long,
    val words: List<Word>,
)

data class Word(
    val englishForm: String,
    val translates: List<String>
)

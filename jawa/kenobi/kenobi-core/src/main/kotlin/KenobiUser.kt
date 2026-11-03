package ru.prohor.universe.kenobi.core

import org.bson.types.ObjectId

data class KenobiUser(
    val id: ObjectId,
    val telegramChatId: Long?,
    val apiToken: String?,
) {
    companion object {
        const val ATTRIBUTE_KEY = "universe.kenobi-user"
    }
}

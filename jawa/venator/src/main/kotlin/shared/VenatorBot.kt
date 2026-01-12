package ru.prohor.universe.venator.shared

import ru.prohor.universe.jocasta.tgbots.BotAuth
import ru.prohor.universe.jocasta.tgbots.DeafBot

class VenatorBot(auth: BotAuth) : DeafBot(auth) {
    override fun onSendingException(e: Exception, chatId: Long) {
        e.printStackTrace()
        // TODO log
    }

    override fun onForbidden(response: String, chatId: Long) {
        println("forbidden for chatId=$chatId, $response")
        // TODO
    }

    override fun onMigrateToSuperGroup(oldChatId: Long, newChatId: Long) {
        println("migrate from $oldChatId to $newChatId")
        // TODO
    }
}

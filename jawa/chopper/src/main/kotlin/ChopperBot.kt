package ru.prohor.universe.chopper.app

import ru.prohor.universe.jocasta.tgbots.BotAuth
import ru.prohor.universe.jocasta.tgbots.DeafBot
import java.lang.Exception

class ChopperBot(auth: BotAuth) : DeafBot(auth) {
    override fun onSendingException(e: Exception, chatId: Long) {
        e.printStackTrace()
        // TODO log
    }

    override fun onForbidden(response: String, chatId: Long) {
        println("onForbidden: $response")
        // TODO log
    }

    override fun onMigrateToSuperGroup(oldChatId: Long, newChatId: Long) {
        // TODO log warn
    }
}

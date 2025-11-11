package ru.prohor.universe.venator.telegram

import ru.prohor.universe.jocasta.tgbots.BotAuth
import ru.prohor.universe.jocasta.tgbots.DeafBot

class VenatorBot(auth: BotAuth) : DeafBot(auth) {
    override fun onSendingException(e: Exception, chatId: String?) {
        e.printStackTrace()
        // TODO log
    }
}

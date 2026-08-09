package ru.prohor.universe.kenobi.plugin.youtube.tg

import ru.prohor.universe.jocasta.tgbots.BotAuth
import ru.prohor.universe.jocasta.tgbots.DeafBot
import java.lang.Exception

class KenobiBot(botAuth: BotAuth) : DeafBot(botAuth) {
    override fun onSendingException(e: Exception, chatId: Long) {
        e.printStackTrace()
    }

    override fun onForbidden(response: String?, chatId: Long) {
        // TODO
    }

    override fun onMigrateToSuperGroup(oldChatId: Long, newChatId: Long) {
        // TODO
    }
}

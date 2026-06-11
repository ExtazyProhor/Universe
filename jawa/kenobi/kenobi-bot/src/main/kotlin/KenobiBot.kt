package ru.prohor.universe.kenobi.bot

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.prohor.universe.jocasta.tgbots.BotAuth
import ru.prohor.universe.jocasta.tgbots.DeafBot
import java.lang.Exception

@Component
class KenobiBot(
    @Value($$"${universe.kenobi.bot.username}") username: String,
    @Value($$"${universe.kenobi.bot.token}") token: String
) : DeafBot(BotAuth(username, token)) {

    override fun onSendingException(e: Exception?, chatId: Long) {
        TODO("Not yet implemented")
    }

    override fun onForbidden(response: String?, chatId: Long) {
        TODO("Not yet implemented")
    }

    override fun onMigrateToSuperGroup(oldChatId: Long, newChatId: Long) {
        TODO("Not yet implemented")
    }
}

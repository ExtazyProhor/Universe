package ru.prohor.universe.yoda.bot

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated
import org.telegram.telegrambots.meta.api.objects.Update
import ru.prohor.universe.jocasta.tgbots.BotSettings
import ru.prohor.universe.jocasta.tgbots.SimpleBot

class YodaBot(settings: BotSettings) : SimpleBot(settings) {
    override fun onHandlingException(e: Exception?) {
        log.error("error while handling", e)
    }

    override fun onSendingException(e: Exception?, chatId: Long) {
        log.error("error while sending to chatId={}", chatId, e)
    }

    override fun onBotAddedToChat(chatId: Long, chat: Chat?) {
        // TODO
    }

    override fun onBotRemovedFromChat(chatId: Long, chat: Chat?) {
        // TODO
    }

    override fun onUnrecognizedChatMember(chatId: Long, chatMemberUpdated: ChatMemberUpdated?) {
        log.warn("unrecognized chatMember: {}", chatMemberUpdated)
    }

    override fun onUnknownAction(update: Update?) {
        log.warn("unknown update: $update")
    }

    override fun onForbidden(response: String?, chatId: Long) {
        log.warn("forbidden for chatId={}, {}", chatId, response)
    }

    override fun onMigrateToSuperGroup(oldChatId: Long, newChatId: Long) {
        log.warn("migrate from {} to {}", oldChatId, newChatId)
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(YodaBot::class.java)
    }
}

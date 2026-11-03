package ru.prohor.universe.kenobi.core

import org.bson.types.ObjectId
import ru.prohor.universe.jocasta.core.security.PasswordGenerator

class KenobiUserService {
    private val passwordGenerator = PasswordGenerator()

    fun createUser(
        id: ObjectId = ObjectId(),
        telegramChatId: Long? = null,
    ): KenobiUser {
        return KenobiUser(
            id = id,
            telegramChatId = telegramChatId,
            apiToken = passwordGenerator.generate(
                length = 32,
                useSpecial = false
            ),
        )
    }
}

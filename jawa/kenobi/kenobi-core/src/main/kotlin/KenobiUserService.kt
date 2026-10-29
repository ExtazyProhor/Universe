package ru.prohor.universe.kenobi.core

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import ru.prohor.universe.jocasta.core.security.PasswordGenerator

@Service
class KenobiUserService {
    private val passwordGenerator = PasswordGenerator()

    fun createUser(
        telegramChatId: Long? = null,
    ): KenobiUser {
        return KenobiUser(
            id = ObjectId(),
            telegramChatId = telegramChatId,
            apiToken = passwordGenerator.generate(
                length = 32,
                useSpecial = false
            ),
        )
    }
}

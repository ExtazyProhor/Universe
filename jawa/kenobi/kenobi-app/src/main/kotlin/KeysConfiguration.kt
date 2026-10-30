package ru.prohor.universe.kenobi.app

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.prohor.universe.jocasta.core.security.rsa.PublicKeyFromStringProvider

@Configuration
class KeysConfiguration {
    @Bean
    fun publicKeyProvider(
        @Value($$"${universe.kenobi.public-key}") publicKey: String
    ) = PublicKeyFromStringProvider(publicKey)
}

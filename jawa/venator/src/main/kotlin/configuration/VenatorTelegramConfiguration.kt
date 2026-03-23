package ru.prohor.universe.venator.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Profile
import ru.prohor.universe.chopper.client.ChopperClientConfig
import ru.prohor.universe.venator.webhook.service.TelegramNotifier

@Configuration
@Profile("local | stable")
@Import(
    TelegramNotifier::class,
    ChopperClientConfig::class,
)
class VenatorTelegramConfiguration

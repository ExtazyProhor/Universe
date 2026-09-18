package ru.prohor.universe.fondor.core

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import ru.prohor.universe.jocasta.spring.configuration.HolocronConfiguration
import ru.prohor.universe.jocasta.spring.configuration.JocastaAutoConfiguration


@Configuration
@Import(
    JocastaAutoConfiguration::class,
    HolocronConfiguration::class,
    SPAController::class,
)
class SPAWebConfig

package ru.prohor.universe.fondor.kenobi

import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import ru.prohor.universe.fondor.core.SPAWebConfig

@Configuration
@Import(SPAWebConfig::class)
class FondorKenobiMain

fun main(args: Array<String>) {
    runApplication<FondorKenobiMain>(*args)
}

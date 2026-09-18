package ru.prohor.universe.fondor.scarif

import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import ru.prohor.universe.fondor.core.SPAWebConfig

@Configuration
@Import(SPAWebConfig::class)
class FondorScarifMain

fun main(args: Array<String>) {
    runApplication<FondorScarifMain>(*args)
}

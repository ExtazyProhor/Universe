package ru.prohor.universe.venator

import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import ru.prohor.universe.jocasta.spring.configuration.HolocronConfiguration
import ru.prohor.universe.jocasta.spring.configuration.JocastaAutoConfiguration

@Configuration
@ComponentScan
@Import(
    JocastaAutoConfiguration::class,
    HolocronConfiguration::class,
)
class Venator

fun main(args: Array<String>) {
    runApplication<Venator>(*args)
}

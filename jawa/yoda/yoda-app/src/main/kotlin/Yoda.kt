package ru.prohor.universe.yoda.app


import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import ru.prohor.universe.jocasta.spring.configuration.HolocronConfiguration
import ru.prohor.universe.jocasta.spring.configuration.JocastaAutoConfiguration
import ru.prohor.universe.yoda.bot.YodaBotConfiguration

@Configuration
@ComponentScan
@Import(
    JocastaAutoConfiguration::class,
    HolocronConfiguration::class,

    YodaBotConfiguration::class,
)
class Yoda

fun main(args: Array<String>) {
    runApplication<Yoda>(*args)
}

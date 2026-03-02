package ru.prohor.universe.bb8

import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import ru.prohor.universe.jocasta.spring.configuration.HolocronConfiguration
import ru.prohor.universe.jocasta.spring.configuration.JocastaAutoConfiguration
import ru.prohor.universe.jocasta.springweb.configuration.RootControllerConfiguration
import ru.prohor.universe.jocasta.springweb.configuration.StaticResourcesHandlerConfiguration

@Configuration
@ComponentScan
@Import(
    JocastaAutoConfiguration::class,
    HolocronConfiguration::class,
    RootControllerConfiguration::class,
    StaticResourcesHandlerConfiguration::class,
)
class BB8

fun main(args: Array<String>) {
    runApplication<BB8>(*args)
}

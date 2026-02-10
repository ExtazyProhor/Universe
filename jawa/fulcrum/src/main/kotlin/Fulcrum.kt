package ru.prohor.universe.fulcrum

import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import ru.prohor.universe.jocasta.spring.configuration.HolocronConfiguration
import ru.prohor.universe.jocasta.spring.configuration.JocastaAutoConfiguration
import ru.prohor.universe.jocasta.springweb.configuration.FilesControllerConfiguration
import ru.prohor.universe.jocasta.springweb.configuration.PagesControllerConfiguration
import ru.prohor.universe.jocasta.springweb.configuration.RootControllerConfiguration
import ru.prohor.universe.jocasta.springweb.configuration.StaticResourcesHandlerConfiguration

@Configuration
@ComponentScan
@Import(
    JocastaAutoConfiguration::class,
    HolocronConfiguration::class,

    StaticResourcesHandlerConfiguration::class,
    PagesControllerConfiguration::class,
    RootControllerConfiguration::class,
    FilesControllerConfiguration::class
)
class Fulcrum

fun main(args: Array<String>) {
    runApplication<Fulcrum>(*args)
}

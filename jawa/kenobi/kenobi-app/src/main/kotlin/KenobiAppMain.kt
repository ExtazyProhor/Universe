package ru.prohor.universe.kenobi.app

import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableScheduling
import ru.prohor.universe.jocasta.core.jackson.JacksonJocastaCoreConfiguration
import ru.prohor.universe.jocasta.core.jackson.JacksonKotlinConfiguration
import ru.prohor.universe.jocasta.morphia.jackson.JacksonMorphiaConfiguration
import ru.prohor.universe.jocasta.spring.configuration.HolocronConfiguration
import ru.prohor.universe.jocasta.spring.configuration.JocastaAutoConfiguration

@EnableScheduling
@Configuration
@ComponentScan
@Import(
    JocastaAutoConfiguration::class,
    HolocronConfiguration::class,
    JacksonMorphiaConfiguration::class,
    JacksonJocastaCoreConfiguration::class,
    JacksonKotlinConfiguration::class,
)
class KenobiAppMain

fun main(args: Array<String>) {
    runApplication<KenobiAppMain>(*args)
}

package ru.prohor.universe.jocasta.cfg.kotlin

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import ru.prohor.universe.jocasta.spring.configuration.JacksonConfiguration

@Configuration
@Import(JacksonConfiguration::class)
class JacksonKotlinConfiguration {
    @Bean
    fun kotlinModule(): Module {
        return KotlinModule.Builder().build()
    }
}

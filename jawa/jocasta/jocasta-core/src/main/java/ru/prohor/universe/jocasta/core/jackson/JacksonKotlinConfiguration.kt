package ru.prohor.universe.jocasta.core.jackson

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(JacksonConfiguration::class)
class JacksonKotlinConfiguration {
    @Bean
    fun createKotlinModule(): Module {
        return kotlinModule()
    }
}

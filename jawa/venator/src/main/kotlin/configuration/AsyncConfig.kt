package ru.prohor.universe.venator.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Configuration
class AsyncConfig {
    @Bean
    fun webhookExecutor(): ExecutorService = Executors.newFixedThreadPool(1)
}

package ru.prohor.universe.kenobi.app

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.prohor.universe.jocasta.morphia.MongoTransactionService
import ru.prohor.universe.jocasta.morphia.impl.MongoInMemoryTransactionService

@Configuration
class KenobiMongoConfiguration {
    @Bean
    fun mongoTransactionService(): MongoTransactionService {
        return MongoInMemoryTransactionService()
    }
}

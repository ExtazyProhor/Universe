package ru.prohor.universe.kenobi.app

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.prohor.universe.jocasta.morphia.MongoRepository
import ru.prohor.universe.jocasta.morphia.MongoTransactionService
import ru.prohor.universe.jocasta.morphia.impl.MongoFileRepository
import ru.prohor.universe.jocasta.morphia.impl.MongoInMemoryTransactionService
import ru.prohor.universe.kenobi.core.KenobiUser

@Configuration
class KenobiMongoConfiguration {
    @Bean
    fun mongoTransactionService(): MongoTransactionService {
        return MongoInMemoryTransactionService()
    }

    @Bean
    fun kenobiUserRepository(
        @Value($$"${universe.kenobi.app.collection-file.kenobi-users}")
        usersCollectionFile: String,
        objectMapper: ObjectMapper
    ): MongoRepository<KenobiUser> {
        return MongoFileRepository(
            KenobiUser::id,
            KenobiUser::class.java,
            usersCollectionFile,
            objectMapper
        )
    }
}

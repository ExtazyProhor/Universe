package ru.prohor.universe.kenobi.plugin.english

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.PropertySource
import ru.prohor.universe.jocasta.core.jackson.JacksonKotlinConfiguration
import ru.prohor.universe.jocasta.morphia.MongoRepository
import ru.prohor.universe.jocasta.morphia.impl.MongoFileRepository
import ru.prohor.universe.jocasta.morphia.jackson.JacksonMorphiaConfiguration
import ru.prohor.universe.kenobi.plugin.english.model.WordsSet

@Configuration
@ComponentScan
@PropertySource("classpath:kenobi-english-plugin.properties")
@Import(
    JacksonKotlinConfiguration::class,
    JacksonMorphiaConfiguration::class,
)
class KenobiEnglishPluginConfiguration {
    @Bean
    fun wordsCollection(
        @Value($$"${universe.kenobi.plugins.english.collection-file.english-words}") wordsFile: String,
        objectMapper: ObjectMapper
    ): MongoRepository<WordsSet> {
        return MongoFileRepository(
            WordsSet::id,
            WordsSet::class.java,
            wordsFile,
            objectMapper
        )
    }
}

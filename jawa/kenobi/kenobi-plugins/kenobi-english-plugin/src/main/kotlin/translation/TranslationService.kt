package ru.prohor.universe.kenobi.plugin.english.translation

import org.springframework.stereotype.Service
import ru.prohor.universe.jocasta.core.features.fieldref.KFR
import ru.prohor.universe.jocasta.morphia.MongoRepository
import ru.prohor.universe.jocasta.morphia.filter.MongoFilters

@Service
class TranslationService(
    private val aiTranslationService: AiTranslationService,
    private val translationsRepository: MongoRepository<WordTranslation>
) {
    // TODO mongo transaction
    fun getTranslations(sources: List<String>): Map<String, WordTranslation> {
        val uniqueSources = sources.filter(String::isNotBlank).distinct()
        if (uniqueSources.isEmpty()) return emptyMap()

        val translations = findAllBySources(uniqueSources)
        val missing = uniqueSources.filterNot(translations::containsKey)
        if (missing.isEmpty()) return translations

        val translated = aiTranslationService.translate(missing)
        translationsRepository.save(translated)
        return translations + translated.associateBy { it.source }
    }

    private fun findAllBySources(sources: List<String>): Map<String, WordTranslation> {
        return translationsRepository.find(MongoFilters.`in`(KFR.wrap(WordTranslation::source), sources))
            .associateBy { it.source }
    }
}

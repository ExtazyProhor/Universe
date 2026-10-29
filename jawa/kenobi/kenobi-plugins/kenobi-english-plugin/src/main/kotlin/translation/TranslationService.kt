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
    fun getTranslation(source: String): WordTranslation? {
        return findBySource(source) ?: translateAndSaveNewSource(source)
    }

    private fun translateAndSaveNewSource(source: String): WordTranslation? {
        val translation = aiTranslationService.translate(source) ?: return null
        translationsRepository.save(translation)
        return translation
    }

    private fun findBySource(source: String): WordTranslation? {
        return translationsRepository.findOne(MongoFilters.eq(KFR.wrap(WordTranslation::source), source)).orElseNull()
    }
}

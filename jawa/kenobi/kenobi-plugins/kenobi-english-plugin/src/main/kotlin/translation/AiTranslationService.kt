package ru.prohor.universe.kenobi.plugin.english.translation

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.genai.Client
import com.google.genai.errors.ClientException
import com.google.genai.types.Content
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.Part
import com.google.genai.types.Schema
import com.google.genai.types.ThinkingConfig
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AiTranslationService(
    @param:Value($$"${universe.kenobi.plugins.english.ai.api-key}")
    private val apiKey: String,
    private val inputDataForTranslationRequestLoader: InputDataForTranslationRequestLoader,
    private val aiRequestInfoService: AIRequestInfoService,
    private val mapper: ObjectMapper
) {
    private val client = Client.builder().apiKey(apiKey).build()

    @Volatile
    private var retryBlockedUntilMillis = 0L

    private val config: GenerateContentConfig by lazy {
        GenerateContentConfig
            .builder()
            .thinkingConfig(ThinkingConfig.builder().thinkingLevel(aiRequestInfoService.thinkingLevel).build())
            .responseMimeType("application/json")
            .responseSchema(Schema.fromJson(inputDataForTranslationRequestLoader.responseSchema))
            .systemInstruction(Content.fromParts(Part.fromText(inputDataForTranslationRequestLoader.systemPrompt)))
            .build()
    }

    fun translate(sources: List<String>): List<WordTranslation> {
        if (System.currentTimeMillis() < retryBlockedUntilMillis) {
            // TODO log
            println("AI translation temporarily disabled after rate limit until $retryBlockedUntilMillis")
            return emptyList()
        }
        if (sources.any { it.isBlank() || it.length > MAX_SOURCE_LENGTH } || sources.size != sources.distinct().size) {
            // TODO log
            println("Illegal translation batch: $sources")
            return emptyList()
        }

        val response = try {
            client.models.generateContent(aiRequestInfoService.model, createContent(sources), config)
        } catch (e: ClientException) {
            if (e.code() == HTTP_TOO_MANY_REQUESTS) {
                retryBlockedUntilMillis = System.currentTimeMillis() + DEFAULT_RETRY_DELAY_SECONDS * 1_000
                // TODO log
                println("AI API rate limit exceeded; requests disabled for $DEFAULT_RETRY_DELAY_SECONDS seconds")
            } else {
                println("AI API error. Code: ${e.code()}, Message: ${e.message}")
                e.printStackTrace()
            }
            return emptyList()
        } catch (e: Exception) {
            // TODO log
            println("Unexpected error during translation: ${e.message}")
            e.printStackTrace()
            return emptyList()
        }

        val json = response.text()
        if (json.isNullOrBlank()) {
            // TODO log warn
            println("Null response from translation AI")
            return emptyList()
        }

        val translations = try {
            mapper.readValue<List<TranslationResponse>>(json)
        } catch (e: Exception) {
            // TODO log with json
            println(json)
            e.printStackTrace()
            return emptyList()
        }

        val responseSources = translations.map { it.source }
        if (
            translations.size != sources.size ||
            responseSources.toSet() != sources.toSet() ||
            translations.any {
                it.translations.isEmpty() || it.translations.any { item ->
                    item.translation.isEmpty() || item.translation.any { word ->
                        word.isBlank()
                    }
                }
            }
        ) {
            // TODO log warn
            println(
                "Invalid translation batch response. Expected sources: $sources, received: $responseSources"
            )
            return emptyList()
        }

        val aiRequestInfoId = aiRequestInfoService.createAndSaveAIRequestInfo(response.usageMetadata())
        return translations.map { translation ->
            WordTranslation(
                id = ObjectId(),
                source = translation.source,
                translations = translation.translations.map {
                    WordTranslationItem(
                        translation = it.translation.map(String::trim).distinct(),
                        example = it.example,
                        exampleTranslation = it.exampleTranslation
                    )
                },
                aiRequestInfoId = aiRequestInfoId
            )
        }
    }

    private fun createContent(sources: List<String>): Content = Content.builder()
        .role("user")
        .parts(Part.fromText(mapper.writeValueAsString(sources)))
        .build()

    private companion object {
        const val MAX_SOURCE_LENGTH = 80
        const val DEFAULT_RETRY_DELAY_SECONDS = 30L
        const val HTTP_TOO_MANY_REQUESTS = 429
    }
}

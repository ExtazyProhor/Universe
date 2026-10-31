package ru.prohor.universe.kenobi.plugin.english.translation

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.genai.Client
import com.google.genai.errors.ClientException
import com.google.genai.types.Content
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.GenerateContentResponseUsageMetadata
import com.google.genai.types.Part
import com.google.genai.types.Schema
import com.google.genai.types.ThinkingConfig
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.prohor.universe.jocasta.core.collections.common.toNullable

@Service
class AiTranslationService(
    @param:Value($$"${universe.kenobi.plugins.english.ai.api-key}")
    private val apiKey: String,
    @param:Value($$"${universe.kenobi.plugins.english.ai.model}")
    private val model: String,
    @param:Value($$"${universe.kenobi.plugins.english.ai.thinking-level}")
    private val thinkingLevel: String,
    private val inputDataForTranslationRequestLoader: InputDataForTranslationRequestLoader,
    private val mapper: ObjectMapper
) {
    private val client = Client.builder().apiKey(apiKey).build()
    private val config: GenerateContentConfig by lazy {
        GenerateContentConfig
            .builder()
            .thinkingConfig(ThinkingConfig.builder().thinkingLevel(thinkingLevel).build())
            .responseMimeType("application/json")
            .responseSchema(Schema.fromJson(inputDataForTranslationRequestLoader.responseSchema))
            .systemInstruction(Content.fromParts(Part.fromText(inputDataForTranslationRequestLoader.systemPrompt)))
            .build()
    }

    fun translate(sources: List<String>): List<WordTranslation> {
        val requestedSources = sources.filter(String::isNotBlank)
            .filter { it.length > MAX_SOURCE_LENGTH }
            .distinct()
        if (sources.size != requestedSources.size) {
            val skipped = sources.toMutableList()
            requestedSources.forEach {
                skipped.remove(it)
            }
            // TODO log
            println("Illegal format of sources / duplicates: ${skipped.joinToString { "'$it'" }}")
        }
        if (requestedSources.isEmpty()) {
            // TODO log
            println("Cannot translate an empty source batch")
            return emptyList()
        }

        val response = try {
            client.models.generateContent(model, createContent(requestedSources), config)
        } catch (e: ClientException) {
            // TODO log
            println("AI API error. Code: ${e.code()}, Message: ${e.message}")
            e.printStackTrace()
            return emptyList()
        } catch (e: Exception) {
            // TODO log
            println("Unexpected error during translation: ${e.message}")
            e.printStackTrace()
            null
        } ?: return emptyList()

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
            translations.size != requestedSources.size ||
            responseSources.toSet() != requestedSources.toSet() ||
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
                "Invalid translation batch response. Expected sources: $requestedSources, received: $responseSources"
            )
            return emptyList()
        }

        val usageMetadata = response.usageMetadata().toNullable()
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
                aiRequestInfo = createAIRequestInfo(usageMetadata)
            )
        }
    }

    private fun createAIRequestInfo(usageMetadata: GenerateContentResponseUsageMetadata?) = AIRequestInfo(
        model = model,
        thinkingLevel = thinkingLevel,
        promptTokenCount = usageMetadata?.promptTokenCount()?.toNullable(),
        outputTokenCount = usageMetadata?.candidatesTokenCount()?.toNullable(),
        thoughtsTokenCount = usageMetadata?.thoughtsTokenCount()?.toNullable(),
        toolUsePromptTokenCount = usageMetadata?.toolUsePromptTokenCount()?.toNullable(),
        totalTokenCount = usageMetadata?.totalTokenCount()?.toNullable()
    )

    private fun createContent(sources: List<String>): Content = Content.builder()
        .role("user")
        .parts(Part.fromText(mapper.writeValueAsString(sources)))
        .build()

    private companion object {
        const val MAX_SOURCE_LENGTH = 80
    }
}

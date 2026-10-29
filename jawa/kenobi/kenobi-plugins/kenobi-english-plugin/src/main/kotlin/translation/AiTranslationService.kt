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

    fun translate(source: String): WordTranslation? {
        if (source.isBlank()) {
            // TODO log
            println("Cannot translate blank source")
            return null
        }

        if (source.length > MAX_SOURCE_LENGTH) {
            println("Source exceeds maximum length of $MAX_SOURCE_LENGTH characters")
            return null
        }

        val response = try {
            client.models.generateContent(model, createContent(source), config)
        } catch (e: ClientException) {
            // TODO log
            println("AI API error. Code: ${e.code()}, Message: ${e.message}")
            e.printStackTrace()
            null
        } catch (e: Exception) {
            // TODO log
            println("Unexpected error during translation: ${e.message}")
            e.printStackTrace()
            null
        } ?: return null

        val json = response.text()
        if (json.isNullOrBlank()) {
            // TODO log warn
            println("Null response from translation AI")
            return null
        }

        val translation = try {
            mapper.readValue<TranslationResponse>(json)
        } catch (e: Exception) {
            // TODO log with json
            println(json)
            e.printStackTrace()
            return null
        }

        if (translation.translations.isEmpty()) {
            // TODO log warn
            println("Empty translations from translation AI")
            return null
        }

        if (source != translation.source) {
            // TODO log warn with both strings
            println("Translation source mismatch. Expected: '$source', received: '${translation.source}'")
        }
        val usageMetadata = response.usageMetadata().toNullable()
        return WordTranslation(
            id = ObjectId(),
            source = source,
            translations = translation.translations.map {
                WordTranslationItem(
                    translation = it.translation.split(",")
                        .map(String::trim)
                        .filter(String::isNotEmpty)
                        .distinct()
                        .joinToString(),
                    example = it.example,
                    exampleTranslation = it.exampleTranslation
                )
            },
            aiRequestInfo = createAIRequestInfo(usageMetadata)
        )
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

    private fun createContent(source: String): Content = Content.builder()
        .role("user")
        .parts(Part.fromText(source))
        .build()

    private companion object {
        const val MAX_SOURCE_LENGTH = 80
    }
}

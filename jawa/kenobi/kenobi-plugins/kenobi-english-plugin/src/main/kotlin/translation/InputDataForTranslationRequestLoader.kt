package ru.prohor.universe.kenobi.plugin.english.translation

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import org.springframework.util.StreamUtils
import java.nio.charset.StandardCharsets

@Component
class InputDataForTranslationRequestLoader(
    @Value("classpath:ai/translate-response-schema.json")
    responseSchema: Resource,
    @Value("classpath:ai/translate-system-prompt.txt")
    systemPrompt: Resource,
    private val mapper: ObjectMapper
) {
    val responseSchema: String by lazy {
        responseSchema.inputStream.use { StreamUtils.copyToString(it, StandardCharsets.UTF_8) }.clearJson(mapper)
    }

    // TODO переписать потом на английский и проверить экономию токенов
    // TODO попробовать другой формат - где сначала идет деление по частям речи
    val systemPrompt: String by lazy {
        systemPrompt.inputStream.use { StreamUtils.copyToString(it, StandardCharsets.UTF_8) }
    }

    private fun String.clearJson(mapper: ObjectMapper): String = mapper.writeValueAsString(mapper.readTree(this))
}

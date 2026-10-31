package ru.prohor.universe.kenobi.plugin.english.translation

import com.google.genai.types.GenerateContentResponseUsageMetadata
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.prohor.universe.jocasta.core.collections.common.toNullable
import ru.prohor.universe.jocasta.morphia.MongoRepository
import java.util.Optional

@Service
class AIRequestInfoService(
    @param:Value($$"${universe.kenobi.plugins.english.ai.model}")
    val model: String,
    @param:Value($$"${universe.kenobi.plugins.english.ai.thinking-level}")
    val thinkingLevel: String,
    private val repository: MongoRepository<AIRequestInfo>
) {
    fun createAndSaveAIRequestInfo(usageMetadata: Optional<GenerateContentResponseUsageMetadata>): ObjectId {
        val info = createAIRequestInfo(usageMetadata.toNullable())
        repository.save(info)
        return info.id
    }

    private fun createAIRequestInfo(usageMetadata: GenerateContentResponseUsageMetadata?) = AIRequestInfo(
        id = ObjectId(),
        model = model,
        thinkingLevel = thinkingLevel,
        promptTokenCount = usageMetadata?.promptTokenCount()?.toNullable(),
        outputTokenCount = usageMetadata?.candidatesTokenCount()?.toNullable(),
        thoughtsTokenCount = usageMetadata?.thoughtsTokenCount()?.toNullable(),
        toolUsePromptTokenCount = usageMetadata?.toolUsePromptTokenCount()?.toNullable(),
        totalTokenCount = usageMetadata?.totalTokenCount()?.toNullable()
    )
}

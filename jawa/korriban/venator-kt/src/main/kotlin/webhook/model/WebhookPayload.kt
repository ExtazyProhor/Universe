package ru.prohor.universe.venator.webhook.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class WebhookPayload(
    val ref: String,
    val repository: RepositoryInfo,
    val sender: SenderInfo,
    @field:JsonProperty("head_commit")
    val headCommit: CommitInfo
)

package ru.prohor.universe.venator.webhook.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class RepositoryInfo(
    @field:JsonProperty("full_name")
    val fullName: String,
    @field:JsonProperty("pushed_at")
    val pushedAt: Long,
    @field:JsonProperty("master_branch")
    val masterBranch: String
)

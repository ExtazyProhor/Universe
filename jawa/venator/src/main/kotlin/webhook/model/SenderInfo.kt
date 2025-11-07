package ru.prohor.universe.venator.webhook.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SenderInfo(
    val login: String
)

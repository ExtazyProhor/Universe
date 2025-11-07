package ru.prohor.universe.venator.webhook.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ApiResponse(
    val success: Boolean,
    val message: String
) {
    companion object {
        fun success(message: String): ApiResponse {
            return ApiResponse(true, message)
        }

        fun error(message: String): ApiResponse {
            return ApiResponse(false, message)
        }
    }
}

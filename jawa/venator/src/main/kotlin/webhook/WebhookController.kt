package ru.prohor.universe.venator.webhook

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.prohor.universe.jocasta.spring.UniverseEnvironment
import ru.prohor.universe.venator.webhook.model.ApiResponse
import ru.prohor.universe.venator.webhook.model.WebhookPayload
import ru.prohor.universe.venator.webhook.service.IpValidationService
import ru.prohor.universe.venator.webhook.service.SignatureService
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/webhook")
class WebhookController(
    @param:Value($$"${universe.venator.git-repo.name}")
    private val repositoryName: String,
    private val environment: UniverseEnvironment,
    private val signatureService: SignatureService,
    private val ipValidationService: IpValidationService,
    private val webhookAction: WebhookAction,
    private val webhookNotifier: WebhookNotifier,
    private val objectMapper: ObjectMapper
) {
    @PostMapping(value = ["/on_push"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun handleWebhook(
        @RequestHeader(value = GITHUB_SIGNATURE_HEADER, required = false) signature: String?,
        @RequestHeader(value = REAL_IP_HEADER, required = false) realIp: String?,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse> {
        val address = if (environment.canBeObtainedLocally() && realIp == null) request.remoteAddr else realIp
        if (address == null)
            return onFailure(HttpStatus.UNAUTHORIZED, "IP address unrecognized")
        if (!ipValidationService.isValidIp(address))
            return onFailure(HttpStatus.UNAUTHORIZED, "Illegal IP address")

        val body = request.bodyAsString()
        val payload = try {
            objectMapper.readValue<WebhookPayload>(body)
        } catch (_: JsonProcessingException) {
            return onFailure(HttpStatus.BAD_REQUEST, "Illegal body structure")
        }

        val actionRepositoryName = payload.repository.fullName
        // TODO log.info("Received webhook from repository: {}", actionRepositoryName);
        if (!signatureService.verifySignature(signature, body)) {
            // TODO log.warn("Invalid webhook signature");
            return onFailure(HttpStatus.UNAUTHORIZED, "Invalid signature")
        }

        if (actionRepositoryName != repositoryName) {
            // TODO log.warn("Webhook from unknown repository: {}", actionRepositoryName);
            return onFailure(HttpStatus.BAD_REQUEST, "Unknown repository")
        }

        val actionBranch = payload.ref.replace("refs/heads/", "")
        if (actionBranch != payload.repository.masterBranch) {
            // TODO log.info("Ignoring push to branch: {}", actionBranch);
            return onInfo("Webhook ignored, wrong branch: $actionBranch")
        }

        processWebhook(payload)
        return onSuccess(payload)
    }

    private fun onFailure(status: HttpStatus, message: String): ResponseEntity<ApiResponse> {
        webhookNotifier.failure(message)
        val response = ApiResponse.error(message)
        return ResponseEntity.status(status).body(response)
    }

    private fun onInfo(message: String): ResponseEntity<ApiResponse> {
        webhookNotifier.info(message)
        val response = ApiResponse.success(message)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    private fun onSuccess(payload: WebhookPayload): ResponseEntity<ApiResponse> {
        webhookNotifier.success(payload)
        val response = ApiResponse.success("Webhook accepted")
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    private fun HttpServletRequest.bodyAsString(): String {
        val bytes: ByteArray = inputStream.readAllBytes()
        return String(
            bytes,
            characterEncoding?.let { Charset.forName(it) } ?: StandardCharsets.UTF_8
        )
    }

    private fun processWebhook(payload: WebhookPayload) {
        try {
            // TODO log.info("Starting webhook processing");
            webhookAction.accept(payload)
            // TODO log.info("Webhook processing completed successfully");
        } catch (e: Exception) {
            webhookNotifier.failure("Error processing webhook: $e")
            // TODO log.error("Error processing webhook", e);
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }

    companion object {
        private const val GITHUB_SIGNATURE_HEADER = "X-Hub-Signature-256"
        private const val REAL_IP_HEADER = "X-Real-IP"
    }
}

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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("IP address unrecognized"))
        if (!ipValidationService.isValidIp(address))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Illegal IP address"))

        val body = request.bodyAsString()
        val payload = try {
            objectMapper.readValue<WebhookPayload>(body)
        } catch (_: JsonProcessingException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Illegal body structure"))
        }

        val actionRepositoryName = payload.repository.fullName
        // TODO log.info("Received webhook from repository: {}", actionRepositoryName);
        if (!signatureService.verifySignature(signature, body)) {
            // TODO log.warn("Invalid webhook signature");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid signature"))
        }

        if (actionRepositoryName != repositoryName) {
            // TODO log.warn("Webhook from unknown repository: {}", actionRepositoryName);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Unknown repository"))
        }

        val actionBranch = payload.ref.replace("refs/heads/", "")
        if (actionBranch != payload.repository.masterBranch) {
            // TODO log.info("Ignoring push to branch: {}", actionBranch);
            return ResponseEntity.ok(ApiResponse.success("Ignored, wrong branch"))
        }

        processWebhook(payload)
        return ResponseEntity.ok(ApiResponse.success("Webhook accepted"))
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
            // TODO log.error("Error processing webhook", e);
            throw RuntimeException(e)
        }
    }

    companion object {
        private const val GITHUB_SIGNATURE_HEADER = "X-Hub-Signature-256"
        private const val REAL_IP_HEADER = "X-Real-IP"
    }
}

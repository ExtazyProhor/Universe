package ru.prohor.universe.venator.webhook

import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.ContentCachingRequestWrapper
import ru.prohor.universe.venator.webhook.model.ApiResponse
import ru.prohor.universe.venator.webhook.model.WebhookPayload

@RestController
@RequestMapping("/webhook")
class WebhookController(
    @param:Value($$"${universe.venator.git-repo.name}")
    private val repositoryName: String,
    private val signatureService: SignatureService,
    private val webhookAction: WebhookAction
) {
    @PostMapping(value = ["/on_push"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun handleWebhook(
        @RequestHeader(value = GITHUB_SIGNATURE_HEADER, required = false) signature: String?,
        @RequestBody payload:  WebhookPayload,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse> {
        val requestWrapper = request as ContentCachingRequestWrapper
        val body = requestWrapper.contentAsString

        val actionRepositoryName = payload.repository.fullName
        // TODO log.info("Received webhook from repository: {}", actionRepositoryName);
        if (!signatureService.verifySignature(signature, body)) {
            // TODO log.warn("Invalid webhook signature");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid signature"))
        }

        if (actionRepositoryName != repositoryName) {
            // TODO log.warn("Webhook from unknown repository: {}", actionRepositoryName);
            return ResponseEntity.ok(ApiResponse.error("Unknown repository"))
        }

        val actionBranch = payload.ref.replace("refs/heads/", "")
        if (actionBranch != payload.repository.masterBranch) {
            // TODO log.info("Ignoring push to branch: {}", actionBranch);
            return ResponseEntity.ok(ApiResponse.success("Ignored, wrong branch"))
        }

        processWebhook(payload)
        return ResponseEntity.ok(ApiResponse.success("Webhook accepted"))
    }

    fun processWebhook(payload: WebhookPayload) {
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
    }
}

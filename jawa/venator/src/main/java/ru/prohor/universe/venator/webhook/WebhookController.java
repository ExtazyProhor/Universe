package ru.prohor.universe.venator.webhook;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
public class WebhookController {
    private static final String GITHUB_SIGNATURE_HEADER = "X-Hub-Signature-256";

    private final String repositoryName;
    private final String branch;
    private final SignatureService signatureService;
    private final WebhookAction webhookAction;

    public WebhookController(
            @Value("${universe.venator.git-repo.name}") String repositoryName,
            @Value("${universe.venator.git-repo.branch}") String branch,
            SignatureService signatureService,
            WebhookAction webhookAction
    ) {
        this.repositoryName = repositoryName;
        this.branch = branch;
        this.signatureService = signatureService;
        this.webhookAction = webhookAction;
    }

    @PostMapping("/on_push")
    public ResponseEntity<ApiResponse> handleWebhook(
            @RequestHeader(value = GITHUB_SIGNATURE_HEADER, required = false) String signature,
            @Valid @RequestBody WebhookPayload payload,
            @RequestBody String body
    ) {
        String actionRepositoryName = payload.repository().fullName();
        //TODO log.info("Received webhook from repository: {}", actionRepositoryName);
        if (!signatureService.verifySignature(signature, body)) {
            // TODO log.warn("Invalid webhook signature");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid signature"));
        }

        if (!actionRepositoryName.equals(repositoryName)) {
            // TODO log.warn("Webhook from unknown repository: {}", actionRepositoryName);
            return ResponseEntity.ok(ApiResponse.error("Unknown repository"));
        }

        String actionBranch = payload.ref().replace("refs/heads/", "");
        if (!actionBranch.equals(branch)) {
            // TODO log.info("Ignoring push to branch: {}", actionBranch);
            return ResponseEntity.ok(ApiResponse.success("Ignored, wrong branch"));
        }

        processWebhook(payload);
        return ResponseEntity.ok(ApiResponse.success("Webhook accepted"));
    }

    @Async
    public void processWebhook(WebhookPayload payload) {
        try {
            // TODO log.info("Starting webhook processing");
            webhookAction.accept(payload);
            // TODO log.info("Webhook processing completed successfully");
        } catch (Exception e) {
            // TODO log.error("Error processing webhook", e);
        }
    }
}

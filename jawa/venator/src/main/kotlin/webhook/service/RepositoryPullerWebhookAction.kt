package ru.prohor.universe.venator.webhook.service

import org.springframework.stereotype.Service
import ru.prohor.universe.jocasta.spring.UniverseEnvironment
import ru.prohor.universe.venator.webhook.WebhookAction
import ru.prohor.universe.venator.webhook.model.WebhookPayload

@Service
class RepositoryPullerWebhookAction(
    private val environment: UniverseEnvironment,
    private val gitService: GitService
) : WebhookAction {
    override fun accept(payload: WebhookPayload) {
        if (environment != UniverseEnvironment.STABLE) {
            // TODO log
            println("Env is not stable, skip repo updating")
        }
        val actionHeadCommitId = payload.headCommit.id
        val currentLastCommit = gitService.lastCommit()
        if (actionHeadCommitId == currentLastCommit) {
            // TODO log warn
            println("Commits before updating are equal")
        }
        gitService.cloneOrPullRepository(payload.repository.cloneUrl, payload.ref.replace("refs/heads/", ""))
        val actualLastCommit = gitService.lastCommit()
        if (actionHeadCommitId != actualLastCommit) {
            // TODO log warn
            println("Commits after updating are not equal")
        }
    }
}

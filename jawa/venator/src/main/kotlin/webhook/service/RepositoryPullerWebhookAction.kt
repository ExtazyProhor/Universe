package ru.prohor.universe.venator.webhook.service

import org.springframework.stereotype.Service
import ru.prohor.universe.jocasta.spring.UniverseEnvironment
import ru.prohor.universe.venator.fs.Repository
import ru.prohor.universe.venator.fs.Timeout
import ru.prohor.universe.venator.webhook.WebhookAction
import ru.prohor.universe.venator.webhook.model.WebhookPayload
import java.nio.file.Path
import java.util.concurrent.TimeUnit

@Service
class RepositoryPullerWebhookAction(
    private val repository: Repository,
    private val environment: UniverseEnvironment,
    private val gitService: GitService
) : WebhookAction {
    override fun accept(payload: WebhookPayload) {
        if (environment != UniverseEnvironment.STABLE) {
            // TODO log
            println("Env is not stable, skip repo updating")
        }
        val wasPerformed = repository.tryPerform(
            operation = { universe -> performPulling(payload, universe) },
            timeout = Timeout(1, TimeUnit.SECONDS)
        )
        if (!wasPerformed)
            throw RuntimeException("Local Universe repository is locked")
    }

    private fun performPulling(payload: WebhookPayload, universe: Path) {
        val branch = payload.ref.replace("refs/heads/", "")
        val actionHeadCommitId = payload.headCommit.id
        val currentLastCommit = gitService.lastCommit(universe)
        if (actionHeadCommitId == currentLastCommit) {
            // TODO log warn
            println("Commits before updating are equal")
        }
        gitService.pullRepository(universe, branch)
        val actualLastCommit = gitService.lastCommit(universe)
        if (actionHeadCommitId != actualLastCommit) {
            // TODO log warn
            println("Commits after updating are not equal")
        }
    }
}

package ru.prohor.universe.venator.webhook.service

import org.springframework.stereotype.Service
import ru.prohor.universe.jocasta.spring.UniverseEnvironment
import ru.prohor.universe.venator.build.service.MavenService
import ru.prohor.universe.venator.fs.Repository
import ru.prohor.universe.venator.fs.Timeout
import ru.prohor.universe.venator.webhook.WebhookAction
import ru.prohor.universe.venator.shared.Notifier
import ru.prohor.universe.venator.webhook.model.WebhookPayload
import java.nio.file.Path
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

@Service
class RepositoryPullerWebhookAction(
    private val notifier: Notifier,
    private val repository: Repository,
    private val environment: UniverseEnvironment,
    private val gitService: GitService,
    private val mavenService: MavenService,
    private val webhookExecutor: ExecutorService
) : WebhookAction {
    override fun accept(payload: WebhookPayload) {
        if (environment != UniverseEnvironment.STABLE) {
            // TODO log
            println("Env is not stable, skip repo updating")
            return
        }
        val wasPerformed = repository.tryPerform(
            operation = { universe -> performPulling(payload, universe) },
            timeout = Timeout(1, TimeUnit.SECONDS)
        )
        if (!wasPerformed)
            throw RuntimeException("Local Universe repository is locked")
        webhookExecutor.submit {
            runTests()
        }
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

    private fun runTests() {
        repository.perform {
            val testResult = mavenService.cleanTestAll()
            if (testResult.success) {
                notifier.success("Tests were passed successfully")
            } else {
                val modulesList = testResult.failedModules
                    .joinToString(separator = "\n") { module ->
                        module.modulePath
                    }
                notifier.failure("${testResult.failedModules.size} modules failed tests:\n$modulesList")
            }
        }
    }
}

package ru.prohor.universe.venator.build.service

import org.springframework.stereotype.Service

@Service
class MavenBuildLogInspector {
    fun inspect(log: List<String>): List<FailedModuleResult> {
        val failedModule = extractFailedModule(log) ?: return emptyList()
        val errorBlock = extractErrorBlock(log)

        return listOf(
            FailedModuleResult(
                modulePath = failedModule,
                failedTests = listOf(
                    FailedTest(
                        className = "<build>",
                        methodName = "<phase>",
                        message = "Build failure",
                        stackTrace = errorBlock
                    )
                )
            )
        )
    }

    private fun extractFailedModule(log: List<String>): String? {
        val reactorIndex = log.indexOfFirst { it.contains("Reactor Summary") }
        if (reactorIndex == -1)
            return null

        return log
            .drop(reactorIndex)
            .firstOrNull { it.contains(" FAILURE") }
            ?.substringBefore(" .")
            ?.trim()
    }

    private fun extractErrorBlock(log: List<String>): String {
        val errorLines = log.filter { it.startsWith("[ERROR]") }
        return errorLines.joinToString("\n")
    }
}

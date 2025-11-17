package ru.prohor.universe.venator.build.service

import org.springframework.stereotype.Service
import ru.prohor.universe.venator.fs.Repository
import ru.prohor.universe.venator.shared.CommandExecutor

@Service
class MavenService(
    repository: Repository,
    private val executor: CommandExecutor,
    private val mavenTestsInspector: MavenTestsInspector
) {
    private val jawa = repository.path.resolve("jawa")
    private val jawaPom = jawa.resolve("pom.xml").toString()

    /**
     * `-f` (file), path to pom.xml
     */
    private val testCommand = listOf("mvn", "-f", jawaPom, "clean", "test")

    fun cleanTestAll(): MavenTestResult {
        return runTest(testCommand)
    }

    /**
     * `-pl` (projects list), path to project
     *
     * `-am` (also make), include dependencies
     *
     * `-amd` (also make dependents), include dependents
     */
    fun cleanTest(modulePath: String): MavenTestResult {
        return runTest(testCommand.plus(listOf("-pl", modulePath, "-am", "-amd")))
    }

    private fun runTest(command: List<String>): MavenTestResult {
        try {
            executor.runCommand(command)
            return MavenTestResult(true, emptyList())
        } catch (e: Exception) {
            // TODO log
            e.printStackTrace()
            return MavenTestResult(false, mavenTestsInspector.inspectTestsFailures(jawa))
        }
    }

    /**
     * `-f` (file), path to pom.xml
     *
     * `-pl` (projects list), path to project
     *
     * `-am` (also make), include dependencies
     */
    fun build(modulePath: String) {
        executor.runCommand(
            command = listOf("mvn", "-f", jawaPom, "package", "-pl", modulePath, "-am", "-DskipTests")
        )
    }
}

data class MavenTestResult(
    val success: Boolean,
    val failedModules: List<FailedModuleResult>
)

data class FailedModuleResult(
    val modulePath: String,
    val failedTests: List<FailedTest>
)

data class FailedTest(
    val className: String,
    val methodName: String,
    val message: String?,
    val stackTrace: String
)

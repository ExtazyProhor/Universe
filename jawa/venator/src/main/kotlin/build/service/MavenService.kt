package ru.prohor.universe.venator.build.service

import org.springframework.stereotype.Service
import ru.prohor.universe.venator.fs.Repository
import ru.prohor.universe.venator.shared.CommandExecutor

@Service
class MavenService(
    repository: Repository,
    private val executor: CommandExecutor,
    private val mavenTestsInspector: MavenTestsInspector,
    private val mavenBuildLogInspector: MavenBuildLogInspector
) {
    private val jawa = repository.path.resolve("jawa")
    private val jawaPom = jawa.resolve("pom.xml").toString()

    /**
     * `-f` (file), path to pom.xml
     *
     * `-B` (batch mode) CI-friendly
     *
     * `-e` (errors) verbose execution error messages, full stack traces
     */
    private val testCommand = listOf("mvn", "-f", jawaPom, "-B", "-e", "-DtrimStackTrace=false", "clean", "test")

    fun cleanTestAll(): MavenTestResult {
        return runTest(testCommand)
    }

    /**
     * `-pl` (projects list), path to project
     *
     * `-am` (also make), include dependencies
     *
     * `-amd` (also make dependents), include dependents
     *
     * `-B` (batch mode) CI-friendly
     *
     * `-e` (errors) verbose execution error messages, full stack traces
     */
    fun cleanTest(modulePath: String): MavenTestResult {
        return runTest(testCommand.plus(listOf("-pl", modulePath, "-B", "-e", "-DtrimStackTrace=false", "-am", "-amd")))
    }

    private fun runTest(command: List<String>): MavenTestResult {
        val result = executor.runCommand(command)
        if (result.exitCode == 0) {
            return MavenTestResult(true, emptyList())
        }
        val testFailures = mavenTestsInspector.inspectTestsFailures(jawa)

        if (testFailures.isNotEmpty()) {
            return MavenTestResult(false, testFailures)
        }

        val buildFailure = mavenBuildLogInspector.inspect(result.fullOutput())
        return MavenTestResult(false, buildFailure)
    }

    /**
     * `-f` (file), path to pom.xml
     *
     * `-pl` (projects list), path to project
     *
     * `-am` (also make), include dependencies
     *
     * `-B` (batch mode) CI-friendly
     *
     * `-e` (errors) verbose execution error messages, full stack traces
     */
    fun build(modulePath: String) {
        executor.runCommand(
            command = listOf(
                "mvn",
                "-f",
                jawaPom,
                "-B",
                "-e",
                "-DtrimStackTrace=false",
                "package",
                "-pl",
                modulePath,
                "-am",
                "-DskipTests"
            )
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

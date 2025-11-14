package ru.prohor.universe.venator.build.service

import org.springframework.stereotype.Service
import org.w3c.dom.Element
import java.nio.file.Files
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

@Service
class MavenTestsInspector {
    fun inspectTestsFailures(jawaPath: Path): List<FailedModuleResult> {
        return findAllTestReports(jawaPath).map { report ->
            FailedModuleResult(
                modulePath = jawaPath.relativize(report.parent.parent).toString(),
                failedTests = inspectModuleFailures(report)
            )
        }.filter { moduleResult -> moduleResult.failedTests.isNotEmpty() }
    }

    private fun findAllTestReports(jawaPath: Path): List<Path> {
        if (!jawaPath.exists())
            return emptyList()

        val result = mutableListOf<Path>()
        val dirsToVisit = ArrayDeque<Path>()
        dirsToVisit.add(jawaPath)

        while (dirsToVisit.isNotEmpty()) {
            val dir = dirsToVisit.removeFirst()
            val pom = dir.resolve("pom.xml")
            if (!pom.exists())
                continue

            val surefire = dir.resolve("target/surefire-reports")
            if (surefire.exists())
                result.add(surefire)

            Files.list(dir)
                .filter { it.isDirectory() }
                .forEach { dirsToVisit.add(it) }
        }
        return result
    }

    private fun inspectModuleFailures(surefireDir: Path): List<FailedTest> {
        if (!surefireDir.exists()) return emptyList()

        val results = mutableListOf<FailedTest>()
        val xmlFiles = surefireDir.toFile()
            .listFiles { f -> f.extension == "xml" }
            ?.toList() ?: return emptyList()

        val factory = DocumentBuilderFactory.newInstance()
        for (file in xmlFiles) {
            val doc = factory.newDocumentBuilder().parse(file)
            val testcases = doc.getElementsByTagName("testcase")

            for (i in 0 until testcases.length) {
                val testcase = testcases.item(i) as Element
                val failures = testcase.getElementsByTagName("failure")
                val errors = testcase.getElementsByTagName("error")

                if (failures.length > 0 || errors.length > 0) {
                    val className = testcase.getAttribute("classname")
                    val methodName = testcase.getAttribute("name")

                    val problemNode = if (failures.length > 0)
                        failures.item(0) as Element
                    else
                        errors.item(0) as Element

                    val message = problemNode.getAttribute("message")
                    val type = problemNode.getAttribute("type")
                    val stackTrace = problemNode.textContent.trim()

                    results += FailedTest(
                        className = className,
                        methodName = methodName,
                        message = if (message.isNullOrBlank()) type else message,
                        stackTrace = stackTrace
                    )
                }
            }
        }
        return results
    }
}

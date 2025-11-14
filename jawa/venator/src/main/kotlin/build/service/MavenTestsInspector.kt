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
    fun inspectTestsFailures(repositoryPath: Path): List<FailedModuleResult> {
        return findAllTestReports(repositoryPath).map { report ->
            FailedModuleResult(
                modulePath = repositoryPath.relativize(report.parent.parent).toString(),
                failedTests = inspectModuleFailures(report)
            )
        }
    }

    private fun findAllTestReports(repositoryPath: Path): List<Path> {
        if (!repositoryPath.exists())
            return emptyList()

        val result = mutableListOf<Path>()
        val dirsToVisit = ArrayDeque<Path>()
        dirsToVisit.add(repositoryPath)

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

                if (failures.length > 0) {
                    val className = testcase.getAttribute("classname")
                    val methodName = testcase.getAttribute("name")
                    val message = failures.item(0).attributes?.getNamedItem("message")?.nodeValue
                    val stackTrace = failures.item(0).textContent.trim()
                    results += FailedTest(
                        className = className,
                        methodName = methodName,
                        message = message,
                        stackTrace = stackTrace
                    )
                }
            }
        }
        return results
    }
}

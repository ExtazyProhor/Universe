package ru.prohor.universe.padawan.kotlin

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class JawaTestsCoverageTest {
    @Test
    fun testNoModulesWithoutTests() {
        val universeHome = System.getenv("UNIVERSE_HOME")
            ?: throw IllegalStateException("UNIVERSE_HOME environment variable is not set")
        val jawaPath = Paths.get(universeHome, "jawa")
        assertTrue(Files.exists(jawaPath), "Base directory does not exist: $jawaPath")

        val mavenModules = Files.walk(jawaPath)
            .filter { Files.isDirectory(it) }
            .filter { dir -> Files.exists(dir.resolve("pom.xml")) }
            .toList()

        val invalidModules = mutableListOf<Path>()
        val jawaHome = "$jawaPath/"

        for (module in mavenModules) {
            if (!Files.exists(module.resolve("src"))) {
                continue
            }

            val testJavaDir = module.resolve("src/test/java")
            val testKotlinDir = module.resolve("src/test/kotlin")

            val hasJavaTests = Files.exists(testJavaDir) && hasTargetFiles(testJavaDir, ".java")
            val hasKotlinTests = Files.exists(testKotlinDir) && hasTargetFiles(testKotlinDir, ".kt")

            if (!hasJavaTests && !hasKotlinTests) invalidModules.add(module)
        }

        assertTrue(invalidModules.isEmpty()) {
            val modules = invalidModules.joinToString("\n") { it.toString().substringAfter(jawaHome) }
            "There are no tests in next modules:\n\n$modules"
        }
    }

    private fun hasTargetFiles(path: Path, extension: String): Boolean {
        return Files.walk(path).use { stream ->
            stream.anyMatch { file ->
                Files.isRegularFile(file) && file.toString().endsWith(extension)
            }
        }
    }
}

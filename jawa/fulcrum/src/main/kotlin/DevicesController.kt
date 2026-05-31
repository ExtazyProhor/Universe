package ru.prohor.universe.fulcrum

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File

@RestController
@RequestMapping("/api/devices")
class DevicesController(
    private val service: DevicesTestService
) {
    @GetMapping("/test")
    fun getTest(): FrontTest {
        return service.getTest()
    }

    @PostMapping("/check")
    fun checkAnswers(
        @RequestBody request: CheckRequest
    ): CheckResult {
        return service.checkAnswers(request)
    }
}

data class DevicesTest(
    val title: String,
    val groups: List<Group>
)

data class Group(
    val name: String,
    val items: List<String>
)

data class FrontTest(
    val title: String,
    val groups: List<String>,
    val items: List<String>
)

data class CheckRequest(
    val answers: Map<String, String>
)

data class CheckResult(
    val correct: Int,
    val total: Int,
    val mistakes: List<Mistake>
)

data class Mistake(
    val item: String,
    val expectedGroup: String,
    val actualGroup: String
)

@Service
class DevicesTestService(
    @Value($$"${universe.fulcrum.devices-file}") devicesFile: String
) {
    private val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule()
    private val test: DevicesTest = mapper.readValue(File(devicesFile))

    fun getTest(): FrontTest {
        val allItems = test.groups.flatMap { it.items }.shuffled()
        return FrontTest(
            title = test.title,
            groups = test.groups.map { it.name },
            items = allItems
        )
    }

    fun checkAnswers(request: CheckRequest): CheckResult {
        val correctMap = buildCorrectMap()
        var correct = 0
        val mistakes = mutableListOf<Mistake>()

        for ((item, selectedGroup) in request.answers) {
            val expectedGroup = correctMap[item]
            if (expectedGroup == selectedGroup) {
                correct++
            } else {
                mistakes += Mistake(
                    item = item,
                    expectedGroup = expectedGroup ?: "unknown",
                    actualGroup = selectedGroup
                )
            }
        }

        return CheckResult(
            correct = correct,
            total = correctMap.size,
            mistakes = mistakes
        )
    }

    private fun buildCorrectMap(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        for (group in test.groups) {
            for (item in group.items) {
                result[item] = group.name
            }
        }
        return result
    }
}

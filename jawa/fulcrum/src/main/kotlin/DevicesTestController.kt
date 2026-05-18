package ru.prohor.universe.fulcrum

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File

@RestController
@RequestMapping("/api/devices-test")
class DevicesTestController(
    private val service: DevicesTest2Service
) {
    @GetMapping
    fun getTest(): FrontDevicesTest2 {
        return service.getTest()
    }

    @PostMapping("/check")
    fun check(
        @RequestBody request: CheckDevicesTestRequest
    ): CheckDevicesTestResponse {

        return service.check(request)
    }
}

data class DevicesTest2(
    val questions: List<DeviceQuestion2>
)

data class DeviceQuestion2(
    val question: String,
    val answers: List<Answer2>
)

data class Answer2(
    val answer: String,
    val right: Boolean
)

data class FrontDevicesTest2(
    val questions: List<FrontQuestion2>
)

data class FrontQuestion2(
    val index: Int,
    val question: String,
    val answers: List<FrontAnswer2>
)

data class FrontAnswer2(
    val index: Int,
    val answer: String
)

data class CheckDevicesTestRequest(
    val answers: Map<Int, List<Int>>
)

data class CheckDevicesTestResponse(
    val correct: Int,
    val total: Int,
    val mistakes: List<QuestionMistake>
)

data class QuestionMistake(
    val questionIndex: Int,
    val expected: List<Int>,
    val actual: List<Int>
)

@Service
class DevicesTest2Service(
    @Value($$"${universe.fulcrum.devices-test-file}") devicesFile: String
) {
    private val test = parseTest(File(devicesFile))

    fun getTest(): FrontDevicesTest2 {
        return FrontDevicesTest2(
            questions = test.questions.mapIndexed { qIndex, q ->
                FrontQuestion2(
                    index = qIndex,
                    question = q.question,
                    answers = q.answers
                        .mapIndexed { aIndex, a ->
                            FrontAnswer2(
                                index = aIndex,
                                answer = a.answer
                            )
                        }
                        .shuffled()
                )
            }
        )
    }

    fun check(
        request: CheckDevicesTestRequest
    ): CheckDevicesTestResponse {
        var correct = 0
        val mistakes = mutableListOf<QuestionMistake>()
        test.questions.forEachIndexed { qIndex, question ->
            val expected = question.answers
                .mapIndexedNotNull { aIndex, answer ->
                    if (answer.right) aIndex else null
                }
                .sorted()

            val actual = request.answers[qIndex]
                ?.sorted()
                ?: emptyList()

            if (expected == actual) {
                correct++
            } else {
                mistakes += QuestionMistake(
                    questionIndex = qIndex,
                    expected = expected,
                    actual = actual
                )
            }
        }

        return CheckDevicesTestResponse(
            correct = correct,
            total = test.questions.size,
            mistakes = mistakes
        )
    }

    private fun parseTest(file: File): DevicesTest2 {
        val questions = file.readLines().chunked(10).map { chunk ->
            DeviceQuestion2(
                question = chunk[0],
                answers = chunk.subList(1, 9).map { answer ->
                    Answer2(
                        answer = answer.removePrefix("+"),
                        right = answer.startsWith("+")
                    )
                }
            )
        }
        return DevicesTest2(questions)
    }
}

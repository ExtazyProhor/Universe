package ru.prohor.universe.fulcrum

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.prohor.universe.jocasta.core.string.StringExtensions
import java.io.File

@RestController
@RequestMapping("/api/test")
class TestController(
    private val test: Test
) {
    @GetMapping("/questions")
    fun getQuestions(): List<QuestionDto> {
        return test.questions.mapIndexed { index, question ->
            QuestionDto(
                index = index,
                question = question.question
            )
        }.shuffled()
    }

    @PostMapping("/answer")
    fun checkAnswer(@RequestBody request: AnswerRequest): AnswerResponse {
        val result = test.checkAnswer(
            answer = request.answer,
            questionIndex = request.questionIndex
        )

        return AnswerResponse(
            isRight = result.isRight,
            rightAnswer = result.rightAnswer
        )
    }
}

@Service
class Test(
    @param:Value($$"${universe.fulcrum.test-file}") private val testFile: String
) {
    val questions = parseQuestions()

    fun checkAnswer(answer: String, questionIndex: Int): AnswerResult {
        val rightAnswer = questions[questionIndex].answer
        val isRight = normalizeAnswer(rightAnswer) == normalizeAnswer(answer)
        return AnswerResult(isRight, rightAnswer)
    }

    private fun normalizeAnswer(answer: String): List<String> {
        return StringExtensions.splitBySpaceChars(answer)
            .map { StringExtensions.sanitizeString(it) }
            .sorted()
    }

    fun parseQuestions(): List<Question> {
        return File(testFile).readLines().chunked(3).map { Question(it[0], it[1]) }
    }
}

data class AnswerResult(
    val isRight: Boolean,
    val rightAnswer: String
)

data class Question(
    val question: String,
    val answer: String
)

data class QuestionDto(
    val index: Int,
    val question: String
)

data class AnswerRequest(
    val questionIndex: Int,
    val answer: String
)

data class AnswerResponse(
    val isRight: Boolean,
    val rightAnswer: String
)

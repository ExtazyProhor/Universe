package ru.prohor.universe.padawan.kotlin.spring

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.prohor.universe.chopper.client.ChopperClient

@RestController
@RequestMapping("/chopper-api")
class TestChopperController(
    @param:Value($$"${universe.padawan.chat-id}")
    private val chatId: Long,
    private val chopperClient: ChopperClient
) {
    @GetMapping("/1")
    fun f1() {
        val success = chopperClient.sendMessage(
            "Hello",
            chatId
        )
        println(success)
    }

    @GetMapping("/2")
    fun f2() {
        val success = chopperClient.sendMessage(
            text = """
                *Жирный текст* и _курсив_
                ~Зачеркнутый~ и ||скрытый|| текст
                [Ссылка на Google](https://google.com)
                `Моноширинный текст`

                *Список:*
                1\. Первый пункт
                2\. Второй пункт
                3\. Третий пункт

                ```python
                # Блок кода (Python)
                def hello_world():
                    print("Hello, Telegram!")
                ```
            """.trimIndent(),
            chatId = chatId,
            markdown = true
        )
        println(success)
    }

    @GetMapping("/3")
    fun f3() {
        val success = chopperClient.sendFile(
            RuntimeException().stackTraceToString(),
            chatId
        )
        println(success)
    }

    @GetMapping("/4")
    fun f4() {
        val success = chopperClient.sendFile(
            "Some some ".repeat(40),
            chatId,
            "my-file.txt",
            "this is SOME"
        )
        println(success)
    }
}

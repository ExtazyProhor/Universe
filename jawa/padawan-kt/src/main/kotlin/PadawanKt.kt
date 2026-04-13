package ru.prohor.universe.kt.padawan

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import ru.prohor.universe.jocasta.cfg.kotlin.JacksonKotlinConfiguration
import ru.prohor.universe.jocasta.spring.configuration.JacksonConfiguration
import ru.prohor.universe.jocasta.spring.features.PrettyJsonPrinter
import ru.prohor.universe.padawan.Padawan
import ru.prohor.universe.padawan.TestFile
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText

object PadawanKt {
    object Jackson {
        val mapper: ObjectMapper = JacksonConfiguration().objectMapper(
            listOf(JacksonKotlinConfiguration().kotlinModule())
        )
        val writer: ObjectWriter = mapper.writer(PrettyJsonPrinter())

        inline fun <reified T> readList(file: TestFile): List<T> {
            return mapper.readValue(
                Padawan.read(file),
                object : TypeReference<List<T>>() {}
            )
        }

        inline fun <reified T> readList(file: String): List<T> {
            return mapper.readValue(
                Path(file).readText(),
                object : TypeReference<List<T>>() {}
            )
        }

        fun <T> writeList(list: List<T>, file: TestFile) {
            Padawan.write(file, writer.writeValueAsString(list))
        }

        fun <T> writeList(list: List<T>, file: String) {
            Path(file).writeText(writer.writeValueAsString(list))
        }
    }
}

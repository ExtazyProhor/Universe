package ru.prohor.universe.venator.shared

import ru.prohor.universe.jocasta.core.string.MarkdownV2

interface Notifier {
    fun failure(message: MarkdownV2)

    fun failure(message: MarkdownV2, fileContent: String, fileName: String)

    fun info(message: MarkdownV2)

    fun success(message: MarkdownV2)
}

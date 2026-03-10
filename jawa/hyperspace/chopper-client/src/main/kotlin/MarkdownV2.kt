package ru.prohor.universe.chopper.client

class MarkdownV2 {
    private val builder = StringBuilder()

    fun text(text: String) = apply {
        builder.append(text.escape())
    }

    fun bold(text: String) = apply {
        builder.append('*')
        builder.append(text.escape())
        builder.append('*')
    }

    fun italic(text: String) = apply {
        builder.append('_')
        builder.append(text.escape())
        builder.append('_')
    }

    fun underline(text: String) = apply {
        builder.append("__")
        builder.append(text.escape())
        builder.append("__")
    }

    fun strike(text: String) = apply {
        builder.append('~')
        builder.append(text.escape())
        builder.append('~')
    }

    fun spoiler(text: String) = apply {
        builder.append("||")
        builder.append(text.escape())
        builder.append("||")
    }

    fun codeInline(text: String) = apply {
        builder.append('`')
        builder.append(text.escape())
        builder.append('`')
    }

    fun codeBlock(code: String, language: String? = null) = apply {
        builder.append("```")
        language?.let { builder.append(it) }
        builder.append('\n')
        builder.append(code)
        builder.append("\n```")
    }

    fun link(text: String, url: String) = apply {
        builder.append('[')
        builder.append(text.escape())
        builder.append("](")
        builder.append(url.escape())
        builder.append(')')
    }

    fun newline() = apply {
        builder.append('\n')
    }

    fun space() = apply {
        builder.append(' ')
    }

    override fun toString(): String {
        return builder.toString()
    }

    private fun String.escape(): String {
        val builder = StringBuilder(length * 2)
        for (c in this) {
            if (CHARS_TO_ESCAPE.contains(c)) {
                builder.append('\\')
            }
            builder.append(c)
        }
        return builder.toString()
    }

    companion object {
        private val CHARS_TO_ESCAPE = "_*[]()~`>#+-=|{}.!".toHashSet()

        fun markdown(block: MarkdownV2.() -> Unit): String {
            return MarkdownV2().apply(block).toString()
        }
    }
}

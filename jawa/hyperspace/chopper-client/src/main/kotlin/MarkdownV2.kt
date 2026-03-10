package ru.prohor.universe.chopper.client

import kotlin.apply

class MarkdownV2 {
    private val nodes = mutableListOf<Node>()

    fun text(text: String) = apply {
        nodes += TextNode(text)
    }

    fun bold(text: String) = apply {
        nodes += BoldNode(text)
    }

    fun italic(text: String) = apply {
        nodes += ItalicNode(text)
    }

    fun strike(text: String) = apply {
        nodes += StrikeNode(text)
    }

    fun spoiler(text: String) = apply {
        nodes += SpoilerNode(text)
    }

    fun codeInline(text: String) = apply {
        nodes += CodeInlineNode(text)
    }

    @JvmOverloads
    fun codeBlock(code: String, lang: String? = null) = apply {
        nodes += CodeBlockNode(code, lang)
    }

    fun link(text: String, url: String) = apply {
        nodes += LinkNode(text, url)
    }

    fun bulletList(vararg items: String) = apply {
        nodes += BulletListNode(items.toList())
    }

    fun bulletList(items: List<String>) = apply {
        nodes += BulletListNode(items)
    }

    fun numberedList(vararg items: String) = apply {
        nodes += NumberedListNode(items.toList())
    }

    fun numberedList(items: List<String>) = apply {
        nodes += NumberedListNode(items)
    }

    fun newline() = apply {
        nodes += CharNode('\n')
    }

    fun space() = apply {
        nodes += CharNode(' ')
    }

    override fun toString() = toMarkdown()

    fun toMarkdown(): String {
        return nodes.joinToString("") { it.markdown() }
    }

    fun toRaw(): String {
        return nodes.joinToString("") { it.raw() }
    }

    companion object {
        fun markdown(block: MarkdownV2.() -> Unit): String {
            return MarkdownV2().apply(block).toString()
        }
    }
}

private class TextNode(private val text: String) : Node {
    override fun markdown() = text.escape()
    override fun raw() = text
}

private class BoldNode(private val text: String) : Node {
    override fun markdown() = "*${text.escape()}*"
    override fun raw() = text
}

private class ItalicNode(private val text: String) : Node {
    override fun markdown() = "_${text.escape()}_"
    override fun raw() = text
}

private class StrikeNode(private val text: String) : Node {
    override fun markdown() = "~${text.escape()}~"
    override fun raw() = text
}

private class SpoilerNode(private val text: String) : Node {
    override fun markdown() = "||${text.escape()}||"
    override fun raw() = text
}

private class CodeInlineNode(private val text: String) : Node {
    override fun markdown() = "`$text`"
    override fun raw() = text
}

private class CodeBlockNode(
    private val code: String,
    private val lang: String?
) : Node {
    override fun markdown(): String {
        return buildString {
            append("```")
            lang?.let { append(it) }
            append("\n")
            append(code)
            append("\n```")
        }
    }

    override fun raw(): String = code
}

private class LinkNode(
    private val text: String,
    private val url: String
) : Node {
    override fun markdown(): String = "[${text.escape()}](${url.escape()})"
    override fun raw(): String = "$text ($url)"
}

private class BulletListNode(private val items: List<String>) : Node {
    override fun markdown(): String {
        return items.joinToString("\n") {
            "• ${it.escape()}"
        } + "\n"
    }

    override fun raw(): String {
        return items.joinToString("\n") {
            "• $it"
        } + "\n"
    }
}

private class NumberedListNode(private val items: List<String>) : Node {
    override fun markdown(): String {
        return items.mapIndexed { i, item ->
            "${i + 1}. $item".escape()
        }.joinToString("\n") + "\n"
    }

    override fun raw(): String {
        return items.mapIndexed { i, item ->
            "${i + 1}. $item"
        }.joinToString("\n") + "\n"
    }
}

private class CharNode(private val char: Char) : Node {
    override fun markdown() = "$char"
    override fun raw() = "$char"
}

private interface Node {
    fun markdown(): String
    fun raw(): String
}

private val CHARS_TO_ESCAPE = "_*[]()~`>#+-=|{}.!".toHashSet()

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

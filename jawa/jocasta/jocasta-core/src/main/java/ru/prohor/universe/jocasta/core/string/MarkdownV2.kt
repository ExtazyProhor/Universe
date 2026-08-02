package ru.prohor.universe.jocasta.core.string

import kotlin.apply

class MarkdownV2 private constructor(private val nodes: MutableList<Node>) {
    constructor() : this(mutableListOf())

    fun text(text: String) = apply {
        nodes += TextNode(text)
    }

    fun bold(text: String) = apply {
        nodes += BoldNode(listOf(TextNode(text)))
    }

    fun bold(block: MarkdownV2.() -> Unit) = apply {
        val child = MarkdownV2().apply(block)
        nodes += BoldNode(child.nodes)
    }

    fun italic(text: String) = apply {
        nodes += ItalicNode(listOf(TextNode(text)))
    }

    fun italic(block: MarkdownV2.() -> Unit) = apply {
        val child = MarkdownV2().apply(block)
        nodes += ItalicNode(child.nodes)
    }

    fun strike(text: String) = apply {
        nodes += StrikeNode(listOf(TextNode(text)))
    }

    fun strike(block: MarkdownV2.() -> Unit) = apply {
        val child = MarkdownV2().apply(block)
        nodes += StrikeNode(child.nodes)
    }

    fun spoiler(text: String) = apply {
        nodes += SpoilerNode(listOf(TextNode(text)))
    }

    fun spoiler(block: MarkdownV2.() -> Unit) = apply {
        val child = MarkdownV2().apply(block)
        nodes += SpoilerNode(child.nodes)
    }

    fun codeInline(text: String) = apply {
        nodes += CodeInlineNode(text)
    }

    @JvmOverloads
    fun codeBlock(code: String, lang: String? = null) = apply {
        nodes += CodeBlockNode(code, lang)
    }

    fun link(text: String, url: String) = apply {
        nodes += LinkNode(listOf(TextNode(text)), url)
    }

    fun link(url: String, block: MarkdownV2.() -> Unit) = apply {
        val child = MarkdownV2().apply(block)
        nodes += LinkNode(child.nodes, url)
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

    fun newline(count: Int = 1) = apply {
        if (count < 1) throw IllegalArgumentException("count must be positive")
        repeat(count) {
            nodes += CharNode('\n')
        }
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

private class BoldNode(children: List<Node>) : ParentNode(children) {
    override fun markdown() = "*${children.joinToString("") { it.markdown() }}*"
}

private class ItalicNode(children: List<Node>) : ParentNode(children) {
    override fun markdown() = "_${children.joinToString("") { it.markdown() }}_"
}

private class StrikeNode(children: List<Node>) : ParentNode(children) {
    override fun markdown() = "~${children.joinToString("") { it.markdown() }}~"
}

private class SpoilerNode(children: List<Node>) : ParentNode(children) {
    override fun markdown() = "||${children.joinToString("") { it.markdown() }}||"
}

private class LinkNode(
    children: List<Node>,
    private val url: String
) : ParentNode(children) {
    override fun markdown(): String {
        val textMarkdown = children.joinToString("") { it.markdown() }
        return "[$textMarkdown](${url.escape()})"
    }
    override fun raw(): String = "${super.raw()} ($url)"
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

private abstract class ParentNode(protected val children: List<Node>) : Node {
    override fun raw(): String = children.joinToString("") { it.raw() }
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

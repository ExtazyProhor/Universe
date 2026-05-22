package ru.prohor.universe.uni.cli.command.string

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.output.MordantHelpFormatter
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.mordant.rendering.TextColors.green
import com.github.ajalt.mordant.rendering.TextColors.yellow
import com.github.ajalt.mordant.rendering.Widget
import com.github.ajalt.mordant.terminal.prompt
import com.github.ajalt.mordant.widgets.Text
import ru.prohor.universe.jocasta.core.utils.NamingStyleUtils
import ru.prohor.universe.uni.cli.command.UniCommand

class CaseSwitch : UniCommand(name = "case") {
    init {
        context {
            helpFormatter = { PreformattedHelpFormatter(it) }
        }
    }

    class PreformattedHelpFormatter(ctx: Context) : MordantHelpFormatter(ctx) {
        override fun renderEpilog(epilog: String): Widget {
            val names = NamingStyleUtils.NamingStyle.entries.map { it.name.lowercase().removeSuffix("_case") }
            val maxLen = names.maxOfOrNull { it.length } ?: 0

            val text = buildString {
                appendLine(styleSectionTitle("Available cases:"))
                NamingStyleUtils.NamingStyle.entries.forEach {
                    val name = it.name.lowercase().removeSuffix("_case")
                    append("  - ")
                    append(styleArgumentName(name))
                    append(" ".repeat(maxLen + 1 - name.length))
                    appendLine(green("'${it.example}'"))
                }
            }
            return Text(text)
        }
    }

    private val input by argument(help = "input string").optional()
    private val from by option("-f", "--from", help = "source naming style").required()
    private val to by option("-t", "--to", help = "target naming style").required()

    override fun help(context: Context) = "changes string-name style"

    override fun helpEpilog(context: Context) = "mock"

    override fun run() {
        val fromStyle = parseStyle(from) ?: return
        val toStyle = parseStyle(to) ?: return

        val string = input ?: terminal.prompt(yellow("Enter string")) ?: return

        val result = changeCase(string, fromStyle, toStyle)
        echo(green(result))
    }

    private fun changeCase(
        string: String,
        from: NamingStyleUtils.NamingStyle,
        to: NamingStyleUtils.NamingStyle
    ): String {
        return NamingStyleUtils.changeStyle(from, to, string)
    }

    private fun parseStyle(raw: String): NamingStyleUtils.NamingStyle? {
        val case = raw.uppercase() + "_CASE"
        val style = NamingStyleUtils.NamingStyle.entries.find { it.name == case }
        if (style == null) errorEcho("Illegal case: $raw")
        return style
    }
}

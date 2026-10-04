package ru.prohor.universe.uni.cli.command.code

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import ru.prohor.universe.uni.cli.command.UniCommand

class AvailableColors : UniCommand("available-colors") {
    override fun help(context: Context) = "show Mordant colors, text styles, and Kotlin usage examples"

    override fun run() {
        echo("Text colors:")
        for (color in TextColors.entries) {
            echo("  ${color.name.padEnd(14)} ${color("sample text")}  TextColors.${color.name}(\"text\")")
        }

        echo("\nBackground colors:")
        val darkBackgrounds = setOf(
            TextColors.black, TextColors.red, TextColors.green, TextColors.blue,
            TextColors.magenta, TextColors.gray, TextColors.brightBlue,
        )
        for (color in TextColors.entries) {
            val foreground = if (color in darkBackgrounds) TextColors.white else TextColors.black
            val sample = (foreground on color)(" sample text ")
            echo("  ${color.name.padEnd(14)} $sample  (TextColors.${foreground.name} on TextColors.${color.name})(\"text\")")
        }

        echo("\nText styles:")
        for (style in TextStyles.entries.filterNot { it.name.startsWith("reset") }) {
            echo("  ${style.name.padEnd(14)} ${style("sample text")}  TextStyles.${style.name}(\"text\")")
        }

        echo("\nMore examples:")
        echo("  ${TextColors.red.bg("red background")} echo(TextColors.red.bg(\"red background\"))")
        echo("  ${(TextColors.green + TextStyles.bold)("bold green")} echo((TextColors.green + TextStyles.bold)(\"bold green\"))")
        echo("  ${TextColors.rgb("#ff8800")("custom RGB text")}  TextColors.rgb(\"#ff8800\")(\"text\")")
        echo("  ${TextColors.rgb("#ff8800").bg("custom RGB background")}  TextColors.rgb(\"#ff8800\").bg(\"text\")")
        echo("  TextColors.rgb(\"#ff8800\", AnsiLevel.ANSI256)(\"text\")")
        echo("  AnsiLevel also supports NONE, ANSI16, and TRUECOLOR")
    }
}

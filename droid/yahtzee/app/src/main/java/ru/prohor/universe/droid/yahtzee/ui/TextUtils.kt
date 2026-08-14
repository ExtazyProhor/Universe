package ru.prohor.universe.droid.yahtzee.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle

@Composable
fun TextStyle.disableDensity(): TextStyle {
    val density = LocalDensity.current
    return copy(
        fontSize = fontSize / density.fontScale,
        lineHeight = lineHeight / density.fontScale
    )
}

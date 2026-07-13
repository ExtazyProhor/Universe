package ru.prohor.universe.droid.yahtzee.ui

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VerticalSpacer(height: Int) {
    Spacer(modifier = Modifier.height(height.dp))
}

@Composable
fun HorizontalSpacer(width: Int) {
    Spacer(modifier = Modifier.width(width.dp))
}

@Composable
fun BoxSpacer(size: Int) {
    Spacer(modifier = Modifier.size(size.dp))
}

@Composable
fun RowScope.ExpandingSpacer(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.weight(1f))
}

@Composable
fun ColumnScope.ExpandingSpacer(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.weight(1f))
}

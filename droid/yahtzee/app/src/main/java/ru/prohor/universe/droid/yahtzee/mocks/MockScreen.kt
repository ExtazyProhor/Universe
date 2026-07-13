package ru.prohor.universe.droid.yahtzee.mocks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ru.prohor.universe.droid.yahtzee.ui.ErrorDialog

@Composable
fun MockScreen() {

}


@Composable
fun MockErrorDialog() {
    var error by remember { mutableStateOf<String?>("error".repeat(100)) }

    error?.let {
        ErrorDialog(
            message = it,
            onDismiss = {
                error = null
            }
        )
    }
}

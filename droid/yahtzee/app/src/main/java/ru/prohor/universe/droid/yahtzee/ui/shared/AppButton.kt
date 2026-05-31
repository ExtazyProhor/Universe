package ru.prohor.universe.droid.yahtzee.ui.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    containerColor: Color = Color.Black,
    contentColor: Color = Color.White,
    imageVector: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(100.dp),
        border = BorderStroke(
            width = 2.dp,
            color = Color.White
        )
    ) {
        text?.let {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
            )
        }

        if (text != null && imageVector != null) {
            BoxSpacer(10)
        }

        imageVector?.let {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

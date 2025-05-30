package com.example.workouttracker.ui.reusable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import com.example.workouttracker.ui.theme.ColorAccent
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

/**
 * The default button displayed in the dialogs
 * @param modifier the modifier of the label
 * @param text the button text
 * @param onClick the onclick event to execute
 * @param enabled whether the button is enabled
 **/
@Composable
fun DialogButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit = {},
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.then(Modifier.fillMaxSize()),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
        ),
        contentPadding = PaddingValues(0.dp),
        shape = RectangleShape,
        enabled = enabled,
    ) {
        Text(text = text, color = ColorAccent)
    }
}

@Preview(widthDp = 360)
@Composable
fun DialogButtonPreview() {
    DialogButton(text = "Save")
}
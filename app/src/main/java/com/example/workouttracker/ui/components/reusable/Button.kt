package com.example.workouttracker.ui.components.reusable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.workouttracker.ui.theme.ColorAccent
import com.example.workouttracker.ui.theme.ColorWhite

/**
 * The default button displayed in the fragments
 * @param modifier the modifier of the label
 * @param text the button text
 * @param onClick the onclick event to execute
 * @param enabled whether the button is enabled
 **/
@Composable
fun Button(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    widthPercent: Float = 0.5f
) {
    OutlinedButton(onClick = onClick,
        modifier = modifier.then(Modifier.fillMaxWidth(widthPercent)),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = ColorAccent),
        border = null,
    )
    {
        Text(text = text, color = ColorWhite)
    }
}


@Preview(widthDp = 360)
@Composable
fun ButtonPreview() {
    Button(text = "Save")
}
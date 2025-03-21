package com.example.workouttracker.ui.components.reusable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.workouttracker.ui.theme.ColorRed

/**
 * Default error label displayed in the app
 * @param text the text displayed in the label
 * @param modifier the modifier of the label
 * @param style the style of the label
 **/
@Composable
fun ErrorLabel(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle = MaterialTheme.typography.labelSmall
) {
    Text(
        text = text,
        modifier = modifier.fillMaxWidth(),
        style = style,
        textAlign = TextAlign.End,
        color = ColorRed
    )
}

@Preview
@Composable
fun CustomErrorLabelPreview() {
    ErrorLabel(text = "Field is invalid")
}
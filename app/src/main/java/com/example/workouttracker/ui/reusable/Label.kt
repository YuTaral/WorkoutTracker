package com.example.workouttracker.ui.reusable

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme

/**
 * Default label displayed in the app
 * @param text the text displayed in the label
 * @param modifier the modifier of the label
 * @param style the style of the label
 * @param maxLines the max lines
 * @param overflow the overflow if the max lines are exceeded
 * @param textAlign the text align
 **/
@Composable
fun Label(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle = MaterialTheme.typography.labelMedium,
    maxLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    textAlign: TextAlign = TextAlign.Center
) {
    Text(
        text = text,
        modifier = modifier,
        style = style,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Preview
@Composable
fun LabelPreview() {
    WorkoutTrackerTheme {
        Label(text = "Main Label")
    }
}

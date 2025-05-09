package com.example.workouttracker.ui.components.reusable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workouttracker.ui.theme.ColorAccent
import com.example.workouttracker.ui.theme.ColorWhite
import com.example.workouttracker.ui.theme.PaddingVerySmall
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import com.example.workouttracker.ui.theme.WorkoutTrackerTheme

/**
 * Custom composable checkbox
 * @param modifier the modifier to apply to the checkbox container
 * @param checked the checked state
 * @param onValueChange the callback to execute on value change,
 * @param text the text to add next to the checkbox
 */
@Composable
fun CheckboxWithLabel(modifier: Modifier = Modifier, checked: Boolean = false, onValueChange: (Boolean) -> Unit, text: String) {
    var checkedVal by rememberSaveable { mutableStateOf(checked) }
    val backgroundColor = if (checkedVal) ColorAccent else ColorWhite

    Row(
        modifier = modifier
            .clickable(
                enabled = true,
                onClick = {
                    checkedVal = !checkedVal
                    onValueChange(checkedVal)
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(backgroundColor)
                .border(1.dp, ColorAccent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (checkedVal) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "",
                    tint = ColorWhite,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        if (!text.isEmpty()) {
            Label(
                modifier = Modifier.padding(start = PaddingVerySmall),
                text = text
            )
        }
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
private fun CheckboxPreview() {
    WorkoutTrackerTheme {
        CheckboxWithLabel(
            modifier = Modifier.fillMaxWidth(),
            checked = true,
            onValueChange = {},
            text = "Exercise Completed"
        )
    }
}
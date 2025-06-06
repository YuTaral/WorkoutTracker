package com.example.workouttracker.ui.reusable

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workouttracker.ui.theme.ColorAccent
import com.example.workouttracker.ui.theme.ColorLightGrey
import com.example.workouttracker.ui.theme.ColorWhite
import com.example.workouttracker.ui.theme.PaddingVerySmall

import com.example.workouttracker.ui.theme.WorkoutTrackerTheme

/**
 * Custom composable checkbox
 * @param modifier the modifier to apply to the checkbox container
 * @param checked the checked state
 * @param onValueChange the callback to execute on value change,
 * @param text the text to add next to the checkbox
 */
@Composable
fun CustomCheckbox(modifier: Modifier = Modifier, checked: Boolean, onValueChange: (Boolean) -> Unit, text: String) {
    val color = if (checked) ColorAccent else ColorLightGrey

    Row(
        modifier = modifier
            .clickable(
                enabled = true,
                onClick = {
                    onValueChange(!checked)
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(color)
                .border(1.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "",
                tint = ColorWhite,
                modifier = Modifier.size(24.dp)
            )
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
private fun CustomCheckboxPreview() {
    WorkoutTrackerTheme {
        CustomCheckbox(
            modifier = Modifier.fillMaxWidth(),
            checked = false,
            onValueChange = {},
            text = "Exercise Completed"
        )
    }
}
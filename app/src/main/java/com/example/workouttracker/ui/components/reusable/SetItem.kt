package com.example.workouttracker.ui.components.reusable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workouttracker.data.models.SetModel
import com.example.workouttracker.ui.theme.ColorAccent
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.ui.theme.SmallImageButtonSize
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.utils.Utils

/**
 * Single set item to display set as part of exercise
 * @param set the set model
 * @param rowNumber the row number
 * @param onRestClick callback to execute when rest for set is clicked
 */
@Composable
fun SetItem(set: SetModel, rowNumber: Int, onRestClick: (Int) -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(PaddingVerySmall)
    ) {
        Label(
            modifier = Modifier.weight(0.15f),
            textAlign = TextAlign.Start,
            text = rowNumber.toString()
        )
        Label(
            modifier = Modifier.weight(0.25f),
            textAlign = TextAlign.Start,
            text = if (set.reps > 0 )set.reps.toString() else ""
        )
        Label(
            modifier = Modifier.weight(0.40f),
            textAlign = TextAlign.Start,
            text = if (set.weight > 0.0) Utils.formatDouble(set.weight) else ""
        )

        Column(modifier = Modifier.weight(0.20f)) {
            if (set.completed) {
                Image(
                    modifier = Modifier
                        .size(SmallImageButtonSize)
                        .clip(CircleShape)
                        .background(ColorAccent),
                    imageVector = Icons.Default.Done,
                    colorFilter = ColorFilter.tint(Color.White),
                    contentDescription = "",
                )
            } else if (set.rest > 0) {
                Box(
                    modifier = Modifier
                        .size(SmallImageButtonSize)
                        .clip(CircleShape)
                        .border(
                            width = 1.dp,
                            color = ColorAccent,
                            shape = CircleShape
                        )
                        .clickable(
                            enabled = true,
                            onClickLabel = null,
                            onClick = { onRestClick(set.reps) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Label(
                        text = set.rest.toString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SetItemPreview() {
    WorkoutTrackerTheme {
        SetItem(SetModel(idVal = 1, repsVal = 12, weightVal = 40.0, restVal = 120, completedVal = true, deletableVal = false), 1, {})
    }
}
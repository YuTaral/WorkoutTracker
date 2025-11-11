package com.example.workouttracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workouttracker.R
import com.example.workouttracker.data.models.TrainingDayModel
import com.example.workouttracker.ui.reusable.ImageButton
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.ui.theme.SmallImageButtonSize
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.ui.theme.labelMediumBold
import com.example.workouttracker.ui.theme.labelMediumGreyItalic
import kotlinx.coroutines.launch

/**
 * Single workout/template displayed in the workouts screen
 * @param trainingDay the training day model
 * @param trainingDayIndex the day index in the program
 * @param onEditClick callback to execute on training day edit click
 * @param showEdit true to show the dit button, false otherwise
 */
@Composable
fun TrainingDayItem(
    trainingDay: TrainingDayModel,
    trainingDayIndex: Int,
    onEditClick: (TrainingDayModel, Int) -> Unit,
    showEdit: Boolean = true
) {
    val scope = rememberCoroutineScope()
    var showWorkouts by rememberSaveable { mutableStateOf(true) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (showWorkouts) 180f else 0f,
        label = "ArrowRotation"
    )

    Column(modifier = Modifier
        .fillMaxWidth()
        .border(
            width = 1.dp,
            color = ColorBorder,
            shape = MaterialTheme.shapes.small
        )
    ) {
        Row(
            modifier = Modifier
                .padding(PaddingVerySmall)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (showEdit) {
                ImageButton(
                    modifier = Modifier.size(SmallImageButtonSize),
                    onClick = { scope.launch { onEditClick(trainingDay, trainingDayIndex + 1) } },
                    image = Icons.Default.Edit,
                    size = SmallImageButtonSize - 5.dp
                )
            }

            Label(
                modifier = Modifier
                    .weight(1f)
                    .padding(PaddingVerySmall),
                text = String.format(stringResource(id = R.string.day_number), trainingDayIndex + 1),
                style = labelMediumBold
            )

            ImageButton(
                modifier = Modifier
                    .size(SmallImageButtonSize)
                    .rotate(rotationAngle),
                onClick = { showWorkouts = !showWorkouts },
                image = Icons.Default.KeyboardArrowDown,
                size = SmallImageButtonSize
            )
        }

        AnimatedVisibility(
            visible = showWorkouts,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            if (trainingDay.workouts.isEmpty()) {
                Label(
                    modifier = Modifier.padding(start = PaddingVerySmall, bottom = PaddingVerySmall),
                    text = stringResource(id = R.string.rest),
                    style = labelMediumGreyItalic
                )
            } else {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = PaddingVerySmall, bottom = PaddingVerySmall),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (workout in trainingDay.workouts) {
                        Label(
                            modifier = Modifier.fillMaxWidth(),
                            text = "- " + workout.name,
                            maxLines = 1,
                            style = labelMediumBold,
                            textAlign = TextAlign.Left
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TrainingDayItemItemPreview() {
    WorkoutTrackerTheme {
        TrainingDayItem(TrainingDayModel(""), 0, onEditClick = { _, _ ->})
    }
}
package com.example.workouttracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.workouttracker.R
import com.example.workouttracker.data.models.AssignedWorkoutModel
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.ui.theme.labelLargeBold
import com.example.workouttracker.ui.theme.labelMediumGrey
import com.example.workouttracker.utils.Utils
import java.util.Date

/**
 * Single assigned workout displayed in the assigned workouts screen
 * @param assignedWorkout the assigned workout model
 * @param onClick callback to execute on assigned workout click
 */
@Composable
fun AssignedWorkoutItem(assignedWorkout: AssignedWorkoutModel, onClick: (AssignedWorkoutModel) -> Unit) {
    var notFinishedStr = stringResource(id = R.string.in_progress_lbl)
    if (assignedWorkout.workoutModel.startDateTime == null) {
        notFinishedStr = stringResource(id = R.string.not_started_lbl)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingVerySmall)
            .clickable(
                enabled = true,
                onClick = { onClick(assignedWorkout) }
            ),
    ) {
        Column(
            modifier  = Modifier
                .fillMaxWidth()
                .padding(start = PaddingVerySmall, bottom = PaddingVerySmall)
        ) {
            Label(
                text = assignedWorkout.teamName + ", " + assignedWorkout.userFullName,
                maxLines = 2,
                style = labelLargeBold
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                Label(
                    modifier = Modifier.padding(end = PaddingVerySmall),
                    text = stringResource(id = R.string.scheduled_for_date),
                    style = labelMediumGrey,
                    textAlign = TextAlign.Left
                )
                Label(
                    text = Utils.defaultFormatDate(assignedWorkout.scheduledForDate),
                    textAlign = TextAlign.Left
                )
            }
        }

        WorkoutItem(
            workout = assignedWorkout.workoutModel,
            onClick = { onClick(assignedWorkout) },
            notFinishedStr = notFinishedStr
        )
    }
}

@Preview
@Composable
fun AssignedWorkoutItemPreview() {
    WorkoutTrackerTheme {
        AssignedWorkoutItem(
            AssignedWorkoutModel(
                WorkoutModel(
                    idVal = 0,
                    nameVal = "Back day",
                    templateVal = false,
                    exercisesVal = mutableListOf(),
                    finishDateTimeVal = Date(),
                    notesVal = "This is the best back day",
                    durationVal = null,
                )
            ),
        onClick = {})
    }
}
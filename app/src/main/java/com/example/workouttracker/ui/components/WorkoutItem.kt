package com.example.workouttracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workouttracker.R
import com.example.workouttracker.data.models.ExerciseModel
import com.example.workouttracker.data.models.SetModel
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.labelMediumGrey
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.ui.theme.labelLargeBold
import com.example.workouttracker.ui.theme.labelMediumGreen
import com.example.workouttracker.ui.theme.labelMediumOrange
import com.example.workouttracker.utils.Utils
import java.util.Date

/**
 * Single workout/template displayed in the workouts screen
 * @param workout the workout model
 * @param weightUnit the selected weight unit
 * @param onClick callback to execute on workout click
 */
@Composable
fun WorkoutItem(workout: WorkoutModel, weightUnit: String, onClick: (WorkoutModel) -> Unit) {
    var exercisesText = ""
    var summaryText = ""
    var totalWeight = 0.0
    var totalReps = 0
    var completedWeight = 0.0
    var completedReps = 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingVerySmall)
            .clickable(
                enabled = true,
                onClick = {
                    onClick(workout)
                }),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Label(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = PaddingVerySmall),
                text = workout.name,
                style = labelLargeBold,
                maxLines = 2,
                textAlign = TextAlign.Left
            )

            if (!workout.template) {
                Column {
                    Label(
                        text = Utils.defaultFormatDateTime(workout.startDateTime!!),
                        style = MaterialTheme.typography.labelSmall
                    )

                    if (workout.finishDateTime != null) {
                        Label(
                            text = Utils.defaultFormatDateTime(workout.finishDateTime!!),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }

        if (!workout.template) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Label(
                        modifier = Modifier.padding(end = PaddingVerySmall),
                        text = stringResource(id = R.string.status_lbl),
                        style = labelMediumGrey
                    )

                    if (workout.finishDateTime == null) {
                        Label(
                            text = stringResource(R.string.in_progress_lbl),
                            style = labelMediumOrange
                        )
                    } else {
                        Label(
                            text = stringResource(R.string.finished_lbl),
                            style = labelMediumGreen
                        )
                    }
                }
            }
        }

        for (e: ExerciseModel in workout.exercises) {
            exercisesText = exercisesText + e.name + ", "

            for (s : SetModel in e.sets) {
                totalWeight += s.weight
                totalReps += s.reps

                if (s.completed) {
                    completedWeight += s.weight
                    completedReps += s.reps
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Label(
                    modifier = Modifier.padding(end = PaddingVerySmall),
                    text = stringResource(id = R.string.exercises_lbl),
                    style = labelMediumGrey
                )
                if (exercisesText.length > 2) {
                    Label(text = exercisesText.dropLast(2))
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            summaryText = if (workout.template) {
                String.format(
                    stringResource(id = R.string.template_summary),
                    Utils.formatDouble(totalWeight), weightUnit, totalReps
                )
            } else {
                String.format(
                    stringResource(id = R.string.workout_summary_lbl),
                    Utils.formatDouble(completedWeight), Utils.formatDouble(totalWeight),
                    weightUnit, completedReps, totalReps
                )
            }

            Label(text = summaryText)
        }

        HorizontalDivider(color = ColorBorder, thickness = 1.dp)
    }
}



@Preview
@Composable
fun WorkoutItemPreview() {
    WorkoutTrackerTheme {
        WorkoutItem(WorkoutModel(
            idVal = 0,
            nameVal = "Back day",
            templateVal = false,
            exercisesVal = mutableListOf(),
            finishDateTimeVal = Date(),
            notesVal = "This is the best back day",
            durationVal = null,
        ),
            "kgs",
            onClick = {})
    }
}
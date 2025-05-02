package com.example.workouttracker.ui.components.fragments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.R
import com.example.workouttracker.data.models.ExerciseModel
import com.example.workouttracker.data.models.SetModel
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.ui.components.reusable.ImageButton
import com.example.workouttracker.ui.components.reusable.Label
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.LabelMediumGrey
import com.example.workouttracker.ui.theme.PaddingMedium
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.ui.theme.labelLargeBold
import com.example.workouttracker.ui.theme.labelMediumGreen
import com.example.workouttracker.ui.theme.labelMediumOrange
import com.example.workouttracker.utils.Utils
import com.example.workouttracker.viewmodel.WorkoutsViewModel
import java.util.Date

@Composable
/**
 * Main Screen to display the latest workouts
 */
fun WorkoutsScreen(vm: WorkoutsViewModel = hiltViewModel()) {
    val workouts by vm.workoutRepository.workouts.collectAsStateWithLifecycle()
    val user by vm.userRepository.user.collectAsStateWithLifecycle()
    val startDate by vm.startDate.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Label(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = PaddingVerySmall),
                    text = stringResource(id = R.string.latest_workouts_lbl),
                    style = labelLargeBold
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PaddingSmall),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Label(
                    text = String.format(
                        stringResource(id = R.string.workouts_filter_lbl),
                        Utils.defaultFormatDate(startDate)
                    ),
                )

                ImageButton(
                    onClick = { vm.showDatePicker() },
                    image = Icons.Default.DateRange
                )
            }

            HorizontalDivider(color = ColorBorder, thickness = 1.dp)

            if (workouts.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(PaddingMedium)
                ) {
                    Label(
                        text = stringResource(id = R.string.no_workouts),
                        style = LabelMediumGrey,
                        maxLines = 2
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState,
                ) {
                    items(workouts) { item ->
                        WorkoutItem(
                            workout = item,
                            weightUnit = user!!.defaultValues.weightUnit.text,
                            onClick = { vm.selectWorkout(item) }
                        )
                    }
                }
            }
        }

        ImageButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = PaddingSmall),
            onClick = { vm.showAddWorkoutDialog() },
            image = Icons.Default.Add
        )
    }
}

@Composable
fun WorkoutItem(workout: WorkoutModel, weightUnit: String, onClick: (WorkoutModel) -> Unit) {
    var exercisesText = ""
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

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Label(
                    modifier = Modifier.padding(end = PaddingVerySmall),
                    text = stringResource(id = R.string.status_lbl),
                    style = LabelMediumGrey
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
                    style = LabelMediumGrey
                )
                if (exercisesText.length > 2) {
                    Label(text = exercisesText.dropLast(2))
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Label(
                    text = String.format(
                        stringResource(id = R.string.workout_summary_lbl),
                        Utils.formatDouble(completedWeight), Utils.formatDouble(totalWeight),
                        weightUnit, completedReps, totalReps
                    )
                )
            }
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
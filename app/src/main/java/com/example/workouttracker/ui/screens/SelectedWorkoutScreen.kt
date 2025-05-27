package com.example.workouttracker.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import com.example.workouttracker.R
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.workouttracker.viewmodel.SelectedWorkoutViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.ui.components.reusable.ExerciseItem
import com.example.workouttracker.ui.components.reusable.ImageButton
import com.example.workouttracker.ui.components.reusable.Label
import com.example.workouttracker.ui.managers.PagerManager
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.LabelMediumGrey
import com.example.workouttracker.ui.theme.LazyListBottomPadding
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.ui.theme.labelLargeBold
import com.example.workouttracker.ui.theme.labelMediumAccent
import com.example.workouttracker.ui.theme.labelMediumGreen
import com.example.workouttracker.ui.theme.labelMediumOrange
import com.example.workouttracker.utils.Utils
import com.example.workouttracker.viewmodel.Page
import kotlinx.coroutines.flow.StateFlow
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Composable
/** The screen displaying the currently selected workout */
fun SelectedWorkoutScreen(vm: SelectedWorkoutViewModel = hiltViewModel<SelectedWorkoutViewModel>()) {
    val selectedWorkout by vm.workoutRepository.selectedWorkout.collectAsStateWithLifecycle()
    val user by vm.userRepository.user.collectAsStateWithLifecycle()

    if (selectedWorkout != null) {
        vm.triggerTimer(start = selectedWorkout!!.finishDateTime == null)
        WorkoutScreen(
            workout = selectedWorkout!!,
            onEditClick =  { vm.showEditWorkoutDialog() },
            secondsStateFlow = vm.secondsElapsed,
            weightUnit = user!!.defaultValues.weightUnit.text,
            onRestClick = { seconds, id -> vm.startTimer(seconds, id) }
        )
    } else {
        vm.stopTimer()
        NoWorkoutScreen(onClick = { vm.showAddWorkoutDialog() })
    }
}

/**
 * Screen to display when workout is selected
 * @param workout the selected workout
 * @param onEditClick callback to execute on edit button click
 * @param secondsStateFlow the state flow of the seconds elapsed
 * @param weightUnit the selected weight unit
 * since the start of the workout
 */
@Composable
private fun WorkoutScreen(
    workout: WorkoutModel,
    onEditClick: () -> Unit,
    secondsStateFlow: StateFlow<Int>,
    weightUnit: String,
    onRestClick: (Long, Long) -> Unit
) {
    var showNotes by rememberSaveable { mutableStateOf(false) }
    val secondsElapsed by secondsStateFlow.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingSmall),
        ) {
            WorkoutDurationTimer(secondsElapsed = secondsElapsed)

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Row {
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

                if (!workout.notes.isEmpty()) {
                    Label(
                        modifier = Modifier
                            .clickable(
                                enabled = true,
                                onClick = {
                                    showNotes = !showNotes
                                }
                            ),
                        text = if (showNotes) stringResource(id = R.string.hide_notes_lbl)
                                else stringResource(id = R.string.show_notes_lbl),
                        style = labelMediumAccent
                    )
                }
            }

            AnimatedVisibility(
                visible = showNotes,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row {
                    Label(
                        modifier = Modifier.padding(end = PaddingVerySmall),
                        text = stringResource(id = R.string.notes_lbl),
                        style = LabelMediumGrey
                    )
                    Label(
                        text = workout.notes,
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Start,
                        maxLines = 10
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(bottom = PaddingSmall),
                color = ColorBorder,
                thickness = 1.dp
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                state = lazyListState,
                contentPadding = PaddingValues(bottom = LazyListBottomPadding)
            ) {
                items(workout.exercises) {  item ->
                    ExerciseItem(
                        exercise = item,
                        weightUnit = weightUnit,
                        onRestClick = { seconds, id -> onRestClick(seconds, id) }
                    )
                }
            }
        }

        ImageButton(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = PaddingSmall),
            onClick = { onEditClick() },
            image = Icons.Default.Edit,
            size = 35.dp
        )

        if (workout.finishDateTime == null) {
            ImageButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = PaddingSmall),
                onClick = {
                    scope.launch {
                        PagerManager.changePageSelection(Page.SelectExercise)
                    }
                },
                image = Icons.Default.Add
            )
        }
    }
}

/**
 * Screen to display when no workout is selected
 * @param onClick callback to execute when user clicks the label
 */
@Composable
private fun NoWorkoutScreen(onClick: () -> Unit) {
    Label(
        modifier = Modifier
            .clickable(
                enabled = true,
                onClick = onClick)
            .padding(PaddingSmall),
        text = stringResource(id = R.string.no_workout_selected_lbl),
        style = LabelMediumGrey,
        maxLines = 3
    )
}

/**
 * Label to display the workout duration
 * @param secondsElapsed the seconds since the start of the workout
 */
@SuppressLint("DefaultLocale")
@Composable
fun WorkoutDurationTimer(secondsElapsed: Int) {
    val hours = secondsElapsed / 3600
    val minutes = (secondsElapsed % 3600) / 60
    val seconds = secondsElapsed % 60

    Label(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = PaddingVerySmall),
        text = String.format("%02d:%02d:%02d", hours, minutes, seconds),
        style = MaterialTheme.typography.titleMedium
    )
}

@Preview
@Composable
@Suppress("UNCHECKED_CAST")
fun WorkoutScreenPreview() {
    WorkoutTrackerTheme {
        WorkoutScreen(
            WorkoutModel(
                idVal = 0,
                nameVal = "Back day",
                templateVal = false,
                exercisesVal = mutableListOf(),
                finishDateTimeVal = Date(),
                notesVal = "This is the best back day",
                durationVal = null,
            ),
            onEditClick = {},
            secondsStateFlow = MutableStateFlow(120).asStateFlow(),
            weightUnit = "Kg", {} as (Long, Long) -> Unit
        )
    }
}



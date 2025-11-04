package com.example.workouttracker.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workouttracker.R
import com.example.workouttracker.data.models.AssignedWorkoutModel
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.ui.components.ExerciseItem
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.LazyListBottomPadding
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.ui.theme.labelLargeBold
import com.example.workouttracker.ui.theme.labelMediumAccent
import com.example.workouttracker.ui.theme.labelMediumGreen
import com.example.workouttracker.ui.theme.labelMediumGrey
import com.example.workouttracker.ui.theme.labelMediumItalic
import com.example.workouttracker.ui.theme.labelMediumOrange
import com.example.workouttracker.utils.Utils
import java.util.Date

/**
 * Screen to view details of an assigned workout
 * @param assignedWorkout the assigned workout model to display
 */
@Composable
fun ViewAssignedWorkoutScreen(assignedWorkout: AssignedWorkoutModel) {
    var showNotes by rememberSaveable { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
    val teamImagePainter = if (!assignedWorkout.teamImage.isEmpty()) {
        val bitmap = Utils.convertStringToBitmap(assignedWorkout.teamImage)
        BitmapPainter(bitmap.asImageBitmap())
    } else {
        painterResource(id = R.drawable.icon_team_default_picture)
    }
    val completed = assignedWorkout.workoutModel.finishDateTime != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingSmall),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = PaddingVerySmall)
            ) {
                Label(
                    text = String.format(stringResource(id = R.string.team_name), assignedWorkout.teamName),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    maxLines = 2,
                )
                Label(
                    text = String.format(stringResource(id = R.string.member_name), assignedWorkout.userFullName),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    maxLines = 2,
                )
                Label(
                    text = String.format(stringResource(id = R.string.scheduled_for_date_same_row),
                                Utils.defaultFormatDate(assignedWorkout.scheduledForDate)),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }

            Image(
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(1.dp, ColorBorder, CircleShape),
                painter = teamImagePainter,
                contentDescription = "Team image"
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = PaddingVerySmall),
            color = ColorBorder,
            thickness = 1.dp
        )

        if (assignedWorkout.workoutModel.durationSeconds != null &&
            assignedWorkout.workoutModel.durationSeconds!! > 0) {
            WorkoutDurationTimer(secondsElapsed = assignedWorkout.workoutModel.durationSeconds!!)
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Label(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = PaddingVerySmall),
                text = assignedWorkout.workoutModel.name,
                style = labelLargeBold,
                maxLines = 2,
                textAlign = TextAlign.Left
            )

            Column {
                if (assignedWorkout.workoutModel.startDateTime != null) {
                    Label(
                        text = Utils.defaultFormatDateTime(assignedWorkout.workoutModel.startDateTime!!),
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                if (assignedWorkout.workoutModel.finishDateTime != null) {
                    Label(
                        text = Utils.defaultFormatDateTime(assignedWorkout.workoutModel.finishDateTime!!),
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
                    style = labelMediumGrey
                )

                if (completed) {
                    Label(
                        text = stringResource(R.string.finished_lbl),
                        style = labelMediumGreen
                    )
                } else {
                    Label(
                        text = if (assignedWorkout.workoutModel.startDateTime == null)
                            stringResource(R.string.not_started_lbl) else
                            stringResource(R.string.in_progress_lbl),
                        style = labelMediumOrange
                    )
                }
            }

            if (!assignedWorkout.workoutModel.notes.isEmpty()) {
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

        if (completed) {
            val daysDifference = Utils.getDateDifferenceInDays(
                assignedWorkout.scheduledForDate,
                assignedWorkout.workoutModel.finishDateTime!!
            )

            if (daysDifference > 0) {
                Label(
                    modifier = Modifier.padding(top = PaddingVerySmall),
                    text = String.format(stringResource(R.string.finished_before), daysDifference),
                    style = labelMediumItalic,
                )
            } else if (daysDifference < 0) {
                Label(
                    modifier = Modifier.padding(top = PaddingVerySmall),
                    text = String.format(stringResource(R.string.finished_after), daysDifference),
                    style = labelMediumItalic,
                )
            }
        }

        AnimatedVisibility(
            visible = showNotes,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Row(modifier = Modifier.padding(top = PaddingVerySmall)) {
                Label(
                    modifier = Modifier.padding(end = PaddingVerySmall),
                    text = stringResource(id = R.string.notes_lbl),
                    style = labelMediumGrey
                )
                Label(
                    text = assignedWorkout.workoutModel.notes,
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
            items(assignedWorkout.workoutModel.exercises) {  item ->
                ExerciseItem(
                    exercise = item,
                    weightUnit = assignedWorkout.workoutModel.weightUnit,
                    hideEdit = true
                )
            }
        }
    }
}

@Preview
@Composable
private fun AssignedWorkoutScreenPreview() {
    WorkoutTrackerTheme {
        ViewAssignedWorkoutScreen(
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
            )
        )
    }
}
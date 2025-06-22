package com.example.workouttracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import com.example.workouttracker.R
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
import com.example.workouttracker.data.models.ExerciseModel
import com.example.workouttracker.data.models.MuscleGroupModel
import com.example.workouttracker.data.models.SetModel
import com.example.workouttracker.ui.reusable.ImageButton
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.labelMediumGrey
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.ui.theme.SmallImageButtonSize
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.ui.theme.labelLargeBold
import kotlinx.coroutines.launch

/**
 * Single exercise item to display exercise as part of workout
 * @param exercise the exercise model
 * @param weightUnit the selected weight unit
 * @param onRestClick callback to execute on the rest timer click
 */
@Composable
fun ExerciseItem(
    exercise: ExerciseModel,
    weightUnit: String,
    onRestClick: (Long, Long) -> Unit,
    showEditExercise: (ExerciseModel, String) -> Unit
) {
    var showSets by rememberSaveable { mutableStateOf(true) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (showSets) 180f else 0f,
        label = "ArrowRotation"
    )
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = PaddingVerySmall)
        .border(
            width = 1.dp,
            color = ColorBorder,
            shape = MaterialTheme.shapes.small
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingVerySmall),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Top
        ) {
            ImageButton(
                modifier = Modifier.size(SmallImageButtonSize),
                onClick = {
                    scope.launch {
                        showEditExercise(exercise, weightUnit)
                    }
                },
                image = Icons.Default.Edit,
                size = SmallImageButtonSize - 5.dp
            )

            Label(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = PaddingVerySmall),
                text = exercise.name,
                maxLines = 3,
                style = labelLargeBold
            )

            ImageButton(
                modifier = Modifier
                    .size(SmallImageButtonSize)
                    .rotate(rotationAngle),
                onClick = { showSets = !showSets },
                image = Icons.Default.KeyboardArrowDown,
                size = SmallImageButtonSize
            )
        }

        AnimatedVisibility(
            visible = showSets,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {

            Column(modifier = Modifier.fillMaxWidth()) {
                Label(
                    modifier = Modifier.padding(start = PaddingVerySmall),
                    text = String.format(
                        stringResource(id = R.string.target_lbl),
                        exercise.muscleGroup.name
                    ),
                    style = labelMediumGrey
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PaddingVerySmall)
                ) {
                    Label(
                        modifier = Modifier.weight(0.15f),
                        textAlign = TextAlign.Start,
                        text = stringResource(id = R.string.set_sequence_lbl)
                    )
                    Label(
                        modifier = Modifier.weight(0.25f),
                        textAlign = TextAlign.Start,
                        text = stringResource(id = R.string.reps_lbl)
                    )
                    Label(
                        modifier = Modifier.weight(0.40f),
                        textAlign = TextAlign.Start,
                        text = String.format(stringResource(id = R.string.weight_lbl), weightUnit),
                    )
                    Label(
                        modifier = Modifier.weight(0.20f),
                        textAlign = TextAlign.Start,
                        text = stringResource(id = R.string.rest_lbl)
                    )
                }

                exercise.sets.forEachIndexed { index, item ->
                    SetItem(
                        set = item,
                        rowNumber = index + 1,
                        onRestClick = { onRestClick(item.rest.toLong(), item.id) }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
@Suppress("UNCHECKED_CAST")
fun ExerciseItemPreview() {
    WorkoutTrackerTheme {
        ExerciseItem(ExerciseModel(
            idVal = 1L,
            nameVal = "Wide pull ups",
            muscleGroupVal = MuscleGroupModel(idVal = 1, nameVal = "Back", imageVal = ""),
            setsVal = mutableListOf<SetModel>(
                SetModel(idVal = 1, repsVal = 12, weightVal = 40.0, restVal = 120, completedVal = true, deletableVal = false),
                SetModel(idVal = 1, repsVal = 8, weightVal = 45.0, restVal = 120, completedVal = true, deletableVal = false),
                SetModel(idVal = 1, repsVal = 7, weightVal = 50.500, restVal = 120, completedVal = false, deletableVal = false),
                SetModel(idVal = 1, repsVal = 4, weightVal = 100.0, restVal = 60, completedVal = false, deletableVal = false),
            ),
            mGExerciseIdVal = 1L,
            notesVal = "Additional notes to exercise"
        ), weightUnit = "Kg", {} as (Long, Long) -> Unit, {} as (ExerciseModel, String) -> Unit)
    }
}
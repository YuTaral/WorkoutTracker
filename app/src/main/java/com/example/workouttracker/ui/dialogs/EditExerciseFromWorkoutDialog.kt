package com.example.workouttracker.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.R
import com.example.workouttracker.data.models.ExerciseModel
import com.example.workouttracker.data.models.MuscleGroupModel
import com.example.workouttracker.data.models.SetModel
import com.example.workouttracker.ui.components.extensions.customBorder
import com.example.workouttracker.ui.components.reusable.DialogButton
import com.example.workouttracker.ui.components.reusable.EditSetItem
import com.example.workouttracker.ui.components.reusable.ImageButton
import com.example.workouttracker.ui.components.reusable.InputField
import com.example.workouttracker.ui.components.reusable.Label
import com.example.workouttracker.ui.theme.DialogFooterSize
import com.example.workouttracker.ui.theme.PaddingLarge
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.viewmodel.EditExerciseFromWorkoutViewModel
import androidx.compose.runtime.getValue

/**
 * Dialog to edit exercise to workout
 * @param exercise the exercise to edit
 * @param weightUnit the weight unit
 */
@Composable
fun EditExerciseFromWorkoutDialog(exercise: ExerciseModel,
                                  weightUnit: String,
                                  vm: EditExerciseFromWorkoutViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        vm.initializeState(exercise)
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PaddingSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            InputField(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = PaddingSmall),
                label = stringResource(id = R.string.additional_notes_lbl),
                value = uiState.notes,
                onValueChange = {
                    if (it.length < 4000) {
                        vm.updateNotes(it)
                    }
                },
                isError = false,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                singleLine = false,
                minLines = 2,
                maxLines = 2
            )

            ImageButton(
                onClick = { vm.showDescription() },
                image = Icons.Default.Info,
            )
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(start = PaddingSmall, end = PaddingSmall, top = PaddingSmall)
        ) {
            Label(
                modifier = Modifier.weight(0.12f),
                text = ""
            )
            Label(
                modifier = Modifier
                    .weight(0.22f)
                    .padding(start = PaddingSmall),
                text = stringResource(R.string.reps_lbl),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.labelSmall
            )
            Label(
                modifier = Modifier
                    .weight(0.28f)
                    .padding(start = PaddingLarge),
                text = String.format(stringResource(R.string.weight_lbl), weightUnit),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.labelSmall
            )
            Label(
                modifier = Modifier
                    .weight(0.22f)
                    .padding(start = PaddingLarge),
                text = stringResource(R.string.rest_lbl),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.labelSmall
            )
        }

        LazyColumn(
            modifier = Modifier.heightIn(min = 170.dp, max = 250.dp)
        ) {
            itemsIndexed(uiState.setsState) { index, set ->
                EditSetItem(
                    setUIState = uiState.setsState[index],
                    deleteModeState = uiState.deleteMode,
                    index = index,
                    onCompletedUpdate = { vm.updateCompleted(index, it) },
                    onDelete = { vm.removeSet(index) },
                    onRepsUpdate = { vm.updateReps(index, it) },
                    onWeightUpdate = { vm.updateWeight(index, it) },
                    onRestUpdate = { vm.updateRest(index, it) }
                )
            }
        }

        Row(modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ImageButton(
                onClick = { vm.updateDeletable(value = !uiState.deleteMode) },
                image = if (uiState.deleteMode) Icons.Default.Done else Icons.Default.Delete
            )

            ImageButton(
                onClick = { vm.addSet() },
                image = Icons.Default.Add
            )
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .height(DialogFooterSize)
        ) {
            DialogButton(
                modifier = Modifier
                    .customBorder(end = true)
                    .weight(1f),
                text = stringResource(R.string.delete_btn),
                onClick = { vm.delete() }
            )

            DialogButton(
                modifier = Modifier
                    .customBorder()
                    .weight(1f),
                text = stringResource(R.string.save_btn),
                onClick = { vm.save() }
            )
        }
    }
}

@Preview
@Composable
fun EditExerciseFromWorkoutDialogPreview() {
    WorkoutTrackerTheme {
        EditExerciseFromWorkoutDialog(ExerciseModel(1, "Wide pull ups", MuscleGroupModel(1,  "Back", ""),
            mutableListOf<SetModel>(
                SetModel(idVal = 1, repsVal = 12, weightVal = 40.0, restVal = 120, completedVal = true, deletableVal = false),
                SetModel(idVal = 1, repsVal = 8, weightVal = 45.0, restVal = 120, completedVal = true, deletableVal = false),
                SetModel(idVal = 1, repsVal = 7, weightVal = 50.500, restVal = 120, completedVal = false, deletableVal = false),
                SetModel(idVal = 1, repsVal = 7, weightVal = 50.500, restVal = 120, completedVal = false, deletableVal = false),
            ), 1, "This is the best back exercise"), "Kg")
    }
}


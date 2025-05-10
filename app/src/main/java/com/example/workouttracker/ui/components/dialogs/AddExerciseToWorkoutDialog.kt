package com.example.workouttracker.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.ui.components.reusable.InputField
import com.example.workouttracker.ui.theme.PaddingMedium
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.viewmodel.AddExerciseToWorkoutViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.R
import com.example.workouttracker.data.models.MGExerciseModel
import com.example.workouttracker.ui.components.extensions.customBorder
import com.example.workouttracker.ui.components.reusable.CustomCheckbox
import com.example.workouttracker.ui.components.reusable.DialogButton
import com.example.workouttracker.ui.theme.DialogFooterSize
import com.example.workouttracker.ui.theme.PaddingLarge

/**
 * Dialog to add exercise to workout
 * @param mGExercise the selected muscle group exercise
 * @param vm the view model
 */
@Composable
fun AddExerciseToWorkoutDialog(mGExercise: MGExerciseModel,
                               weightUnit: String,
                               vm: AddExerciseToWorkoutViewModel = hiltViewModel()
) {
    LaunchedEffect(mGExercise.id) {
        vm.initializeData(mGExercise)
    }

    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val setsFocusRequester = remember { FocusRequester() }
    val repsFocusRequester = remember { FocusRequester() }
    val weightFocusRequester = remember { FocusRequester() }
    val restFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PaddingMedium)
    ) {
        InputField(
            modifier = Modifier.padding(horizontal = PaddingSmall),
            label = stringResource(id = R.string.additional_notes_lbl),
            value = uiState.notes,
            onValueChange = {
                if (it.length < 4000) {
                    vm.updateNotes(it)
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { setsFocusRequester.requestFocus() }),
            singleLine = false,
            minLines = 3,
            maxLines = 3
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PaddingLarge)
        ) {
            InputField(
                modifier = Modifier
                    .padding(horizontal = PaddingSmall)
                    .weight(1f)
                    .focusRequester(setsFocusRequester),
                label = stringResource(id = R.string.sets_lbl),
                value = uiState.sets,
                onValueChange = { vm.updateSets(it) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(onNext = { repsFocusRequester.requestFocus() }),
            )

            InputField(
                modifier = Modifier
                    .padding(horizontal = PaddingSmall)
                    .weight(1f)
                    .focusRequester(repsFocusRequester),
                label = stringResource(id = R.string.reps_lbl),
                value = uiState.reps,
                onValueChange = { vm.updateReps(it) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(onNext = { weightFocusRequester.requestFocus() }),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PaddingLarge)
        ) {
            InputField(
                modifier = Modifier
                    .padding(horizontal = PaddingSmall)
                    .weight(1f)
                    .focusRequester(weightFocusRequester),
                label = String.format(stringResource(id = R.string.weight_lbl), weightUnit),
                value = uiState.weight,
                onValueChange = { vm.updateWeight(it) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(onNext = { restFocusRequester.requestFocus() }),
            )

            InputField(
                modifier = Modifier
                    .padding(horizontal = PaddingSmall)
                    .weight(1f)
                    .focusRequester(restFocusRequester),
                label = stringResource(id = R.string.rest_lbl),
                value = uiState.rest,
                onValueChange = { vm.updateRest(it) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            )
        }

        CustomCheckbox(
            modifier = Modifier.padding(horizontal = PaddingSmall),
            checked = uiState.completed,
            onValueChange = { vm.updateCompleted(it) },
            text = stringResource(id = R.string.exercise_completed_lbl)
        )

        Row(modifier = Modifier
            .fillMaxWidth()
            .height(DialogFooterSize)
        ) {
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
fun AddExerciseToWorkoutDialogPreview() {
    WorkoutTrackerTheme {
        AddExerciseToWorkoutDialog(MGExerciseModel(1L, "Wide pull ups", "", 1L), "Kg")
    }
}
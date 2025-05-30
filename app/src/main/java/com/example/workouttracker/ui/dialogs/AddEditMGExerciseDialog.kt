package com.example.workouttracker.ui.dialogs

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.R
import com.example.workouttracker.data.models.MGExerciseModel
import com.example.workouttracker.ui.extensions.customBorder
import com.example.workouttracker.ui.reusable.CustomCheckbox
import com.example.workouttracker.ui.reusable.DialogButton
import com.example.workouttracker.ui.reusable.ErrorLabel
import com.example.workouttracker.ui.reusable.InputField
import com.example.workouttracker.ui.theme.DialogFooterSize
import com.example.workouttracker.ui.theme.PaddingMedium
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.viewmodel.AddEditMGExerciseViewModel

/**
 * Add / edit muscle group exercise dialog content
 * @param mGExercise the workout to edit if in edit mode, null otherwise
 * @param muscleGroupId the selected muscle group id
 * @param manageExerciseActive whether the dialog is opened when managing exercises
 * @param onSaveCallback callback to execute when add/update exercise is successful
 */
@Composable
fun AddEditMGExerciseDialog(
    mGExercise: MGExerciseModel?,
    muscleGroupId: Long,
    manageExerciseActive: Boolean,
    onSaveCallback: (Boolean, List<String>) -> Unit,
    vm: AddEditMGExerciseViewModel = hiltViewModel()
) {
    val notesFocusReq = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(mGExercise) {
        // Initialize the fields
        vm.initialize(
            mGExercise = mGExercise,
            muscleGroupId = muscleGroupId,
            manageExerciseActiveVal = manageExerciseActive,
            onSaveCallback = onSaveCallback
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PaddingMedium)
    ) {
        InputField(
            modifier = Modifier.padding(horizontal = PaddingSmall),
            label = stringResource(id = R.string.exercise_name_lbl),
            value = uiState.name,
            onValueChange = {
                if (it.length < 50) {
                    vm.updateName(it)
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { notesFocusReq.requestFocus() }),
            isError = uiState.nameError != null
        )

        uiState.nameError?.let {
            ErrorLabel(
                modifier = Modifier.padding(horizontal = PaddingSmall),
                text = uiState.nameError!!
            )
        }

        InputField(
            modifier = Modifier.
            padding(horizontal = PaddingSmall)
                .focusRequester(notesFocusReq),
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
            minLines = 4,
            maxLines = 4
        )

        if (uiState.showAddExToWorkout != null) {
            CustomCheckbox(
                modifier = Modifier.padding(horizontal = PaddingSmall),
                checked = uiState.showAddExToWorkout!!,
                onValueChange = { vm.updateAddExerciseToWorkout(it) },
                text = stringResource(id = R.string.add_exercise_to_workout_lbl)
            )
        }

        Row(modifier = Modifier
            .height(DialogFooterSize)
            .fillMaxWidth()
            .padding(top = PaddingMedium),
            horizontalArrangement = Arrangement.Center
        ) {
            DialogButton(
                modifier = Modifier
                    .customBorder()
                    .weight(1f),
                text = stringResource(R.string.save_btn),
                onClick = { vm.saveExercise(mGExercise == null) }
            )
        }
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
@Suppress("UNCHECKED_CAST")
private fun DialogPreview() {
    WorkoutTrackerTheme {
        AddEditMGExerciseDialog(MGExerciseModel(1L,"Wide pull ups", "", 1L), 1L, false,
            {} as (Boolean, List<String>) -> Unit)
    }
}
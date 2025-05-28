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
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.ui.components.extensions.customBorder
import com.example.workouttracker.ui.components.reusable.DialogButton
import com.example.workouttracker.ui.components.reusable.ErrorLabel
import com.example.workouttracker.ui.components.reusable.InputField
import com.example.workouttracker.ui.theme.DialogFooterSize
import com.example.workouttracker.ui.theme.PaddingMedium
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.viewmodel.AddEditWorkoutViewModel.Mode
import com.example.workouttracker.viewmodel.AddEditWorkoutViewModel
import java.util.Date

/**
 * Add / edit workout dialog content
 * @param workout the workout to edit if in edit mode, null otherwise
 * @param mode the dialog mode
 */
@Composable
fun AddEditWorkoutDialog(workout: WorkoutModel?, mode: Mode, vm: AddEditWorkoutViewModel = hiltViewModel()) {
    val notesFocusReq = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    var nameInputId = R.string.workout_name_lbl

    if (workout != null && workout.template && mode == Mode.EDIT) {
        nameInputId = R.string.template_name_lbl
    }

    LaunchedEffect(workout, mode) {
        // Initialize the fields
        vm.initialize(workout = workout, dialogMode = mode)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PaddingMedium)
    ) {
        InputField(
            modifier = Modifier.padding(horizontal = PaddingSmall),
            label = stringResource(id = nameInputId),
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

        Row(modifier = Modifier
            .height(DialogFooterSize)
            .fillMaxWidth()
            .padding(top = PaddingMedium),
            horizontalArrangement = Arrangement.Center
        ) {
            if (mode == Mode.EDIT && !workout!!.template) {
                DialogButton(
                    modifier = Modifier
                        .customBorder(end = true)
                        .weight(1f),
                    text = stringResource(R.string.delete_btn),
                    onClick = { vm.deleteWorkout() }
                )
            }

            DialogButton(
                modifier = Modifier
                    .customBorder()
                    .weight(1f),
                text = stringResource(R.string.save_btn),
                onClick = { vm.saveWorkout() }
            )
        }
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
private fun DialogPreview() {
    WorkoutTrackerTheme {
        AddEditWorkoutDialog(WorkoutModel(
                idVal = 0,
                nameVal = "Back day",
                templateVal = false,
                exercisesVal = mutableListOf(),
                finishDateTimeVal = Date(),
                notesVal = "This is the best back day",
                durationVal = null,
            ), Mode.EDIT,
        )
    }
}
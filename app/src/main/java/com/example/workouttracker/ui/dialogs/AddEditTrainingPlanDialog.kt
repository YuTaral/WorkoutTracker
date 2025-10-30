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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.R
import com.example.workouttracker.ui.extensions.customBorder
import com.example.workouttracker.ui.reusable.DialogButton
import com.example.workouttracker.ui.reusable.ErrorLabel
import com.example.workouttracker.ui.reusable.InputField
import com.example.workouttracker.ui.theme.DialogFooterSize
import com.example.workouttracker.ui.theme.PaddingMedium
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.viewmodel.AddEditTrainingPlanViewModel

/** Dialog to add / edit training program */
@Composable
fun AddEditTrainingPlanDialog(vm: AddEditTrainingPlanViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        vm.initializeData()
    }

    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val trainingProgram by vm.trainingProgramRepository.selectedTrainingProgram.collectAsStateWithLifecycle()
    val descriptionFocusReq = remember { FocusRequester() }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PaddingSmall),
            verticalArrangement = Arrangement.spacedBy(PaddingSmall)
        )  {
            InputField(
                label = stringResource(id = R.string.training_plan_name),
                value = uiState.name,
                onValueChange = { if (it.length < 50) vm.updateName(it) },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { descriptionFocusReq.requestFocus() }),
                isError = uiState.nameError != null
            )

            uiState.nameError?.let {
                ErrorLabel(
                    modifier = Modifier.padding(horizontal = PaddingSmall),
                    text = uiState.nameError!!
                )
            }

            InputField(
                modifier = Modifier.focusRequester(descriptionFocusReq),
                label = stringResource(id = R.string.training_plan_description),
                value = uiState.description,
                onValueChange = { if (it.length < 4000) vm.updateDescription(it) },
                isError = false,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                singleLine = false,
                minLines = 3,
                maxLines = 3
            )
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = PaddingMedium)
            .height(DialogFooterSize)
        ) {

            if (trainingProgram != null) {
                DialogButton(
                    modifier = Modifier
                        .customBorder()
                        .weight(1f),
                    text = stringResource(R.string.delete_btn),
                    onClick = { vm.delete() }
                )
            }

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
private fun AddEditTrainingPlanDialogPreview() {
    WorkoutTrackerTheme {
        AddEditTrainingPlanDialog()
    }
}
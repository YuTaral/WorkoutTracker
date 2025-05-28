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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.ui.theme.labelMediumGrey
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.R
import com.example.workouttracker.ui.components.extensions.customBorder
import com.example.workouttracker.ui.components.reusable.CustomCheckbox
import com.example.workouttracker.ui.components.reusable.DialogButton
import com.example.workouttracker.ui.components.reusable.InputField
import com.example.workouttracker.ui.components.reusable.Label
import com.example.workouttracker.ui.components.reusable.TwoTextsSwitch
import com.example.workouttracker.ui.theme.DialogFooterSize
import com.example.workouttracker.viewmodel.ExerciseDefaultValuesViewModel
import androidx.compose.runtime.getValue
import com.example.workouttracker.data.models.UserDefaultValuesModel

/**
 * Dialog to change exercise / global default values
 * @param values the default values - null of global, not null if exercise specific
 * @param exerciseName the exercise name, may be empty string if the values are global
 */
@Composable
fun ExerciseDefaultValuesDialog(
    values: UserDefaultValuesModel?,
    exerciseName: String,
    vm: ExerciseDefaultValuesViewModel = hiltViewModel()
) {
    LaunchedEffect(values, exerciseName) {
        vm.initializeData(values)
    }

    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val setsFocusRequester = remember { FocusRequester() }
    val repsFocusRequester = remember { FocusRequester() }
    val weightFocusRequester = remember { FocusRequester() }
    val restFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PaddingSmall)
    ) {
        if (values != null) {
            Label(
                modifier = Modifier.padding(horizontal = PaddingSmall),
                text = String.format(stringResource(R.string.default_values_explanation_exercise_lbl), exerciseName),
                style = labelMediumGrey,
                textAlign = TextAlign.Start,
                maxLines = 4
            )
        } else {
            Label(
                modifier = Modifier.padding(horizontal = PaddingSmall),
                text = stringResource(R.string.default_values_explanation_lbl),
                style = labelMediumGrey,
                textAlign = TextAlign.Start,
                maxLines = 4
            )
        }

        Row(modifier = Modifier.fillMaxWidth()) {
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

        Row( modifier = Modifier.fillMaxWidth()) {
            InputField(
                modifier = Modifier
                    .padding(horizontal = PaddingSmall)
                    .weight(1f)
                    .focusRequester(weightFocusRequester),
                label = String.format(stringResource(id = R.string.weight_lbl), uiState.weightUnit),
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TwoTextsSwitch(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = PaddingSmall),
                selectedValue = uiState.weightUnit,
                leftText = stringResource(id = R.string.weight_unit_kg_lbl),
                rightText = stringResource(id = R.string.weight_unit_lb_lbl),
                disabled = uiState.disableWeightUnit,
                onSelectionChanged = { vm.updateWeightUnit(it) }
            )

            CustomCheckbox(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = PaddingSmall),
                checked = uiState.completed,
                onValueChange = { vm.updateCompleted(it) },
                text = stringResource(id = R.string.completed_lbl)
            )
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .height(DialogFooterSize)
            .padding(top = PaddingSmall)
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
private fun ExerciseDefaultValuesDialogPreview() {
    WorkoutTrackerTheme {
        ExerciseDefaultValuesDialog(null, "")
    }
}
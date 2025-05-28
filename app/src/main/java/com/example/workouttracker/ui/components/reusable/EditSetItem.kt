package com.example.workouttracker.ui.components.reusable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.workouttracker.ui.theme.ColorAccent
import com.example.workouttracker.ui.theme.PaddingLarge
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.ui.theme.SmallImageButtonSize
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.viewmodel.EditExerciseFromWorkoutViewModel.SetUiState

/**
 * Single set item to display set as part of exercise
 * @param set the set model
 */
@Composable
fun EditSetItem(setUIState: SetUiState,
                deleteModeState: Boolean,
                index: Int,
                onCompletedUpdate: (Boolean) -> Unit,
                onDelete: (Int) -> Unit,
                onRepsUpdate: (String) -> Unit,
                onWeightUpdate: (String) -> Unit,
                onRestUpdate: (String) -> Unit
) {
    val weightFocusRequester = remember { FocusRequester() }
    val restFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(start = PaddingSmall, end = PaddingSmall, top = PaddingVerySmall),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(modifier = Modifier.weight(0.12f)) {
            if (deleteModeState) {
                ImageButton(
                    modifier = Modifier.height(SmallImageButtonSize),
                    onClick = { onDelete(index) },
                    image = Icons.Default.Delete,
                    size = SmallImageButtonSize,
                    buttonColor = Color.Transparent,
                    imageColor = ColorAccent
                )
            } else {
                CustomCheckbox(
                    checked = setUIState.completed,
                    onValueChange = { onCompletedUpdate(it) },
                    text = ""
                )
            }
        }

        SmallInputField(modifier = Modifier
                .padding(start = PaddingSmall)
                .weight(0.22f),
            value = setUIState.reps,
            onValueChange = { onRepsUpdate(it) },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions(onNext = { weightFocusRequester.requestFocus() })
        )

        SmallInputField(modifier = Modifier
                .padding(start = PaddingLarge)
                .weight(0.28f)
                .focusRequester(weightFocusRequester),
            value = setUIState.weight,
            onValueChange = { onWeightUpdate(it) },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions(onNext = { restFocusRequester.requestFocus() }),
        )

        SmallInputField(modifier = Modifier
                .padding(start = PaddingLarge)
                .weight(0.22f)
                .focusRequester(restFocusRequester),
            value = setUIState.rest,
            onValueChange = { onRestUpdate(it) },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
        )
    }
}

@Preview
@Composable
fun EditSetItemPreview() {
    WorkoutTrackerTheme {
        EditSetItem(SetUiState(), false, 1, {},
            {}, {}, {}, {})
    }
}
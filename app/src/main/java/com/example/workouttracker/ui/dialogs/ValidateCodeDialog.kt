package com.example.workouttracker.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.R
import com.example.workouttracker.ui.reusable.ErrorLabel
import com.example.workouttracker.ui.reusable.FragmentButton
import com.example.workouttracker.ui.reusable.InputField
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.PaddingMedium
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.viewmodel.ValidateCodeViewModel

/** Edit profile dialog to allow the user to change name / profile picture */
@Composable
fun ValidateCodeDialog(email: String, codeSent: Boolean, validateFor: ValidateCodeViewModel.ValidateFor, vm: ValidateCodeViewModel = hiltViewModel()) {
    val uiState by vm.validateCodeUIState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        vm.initialize(
            email = email,
            codeSent = codeSent,
            validateForValue = validateFor
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = PaddingMedium, end = PaddingMedium, bottom = PaddingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PaddingSmall)
    ) {

        InputField(
            label = stringResource(id = R.string.email_lbl),
            value = uiState.email,
            onValueChange = { vm.updateEmail(it) },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            isError = uiState.emailError != null
        )

        if (uiState.emailError != null) {
            ErrorLabel(text = uiState.emailError!!)
        }

        if (uiState.codeSent) {

            if (uiState.resendTimer > 0) {
                FragmentButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = String.format(stringResource(id = R.string.resend_code_btn), uiState.resendTimer),
                    onClick = { vm.sendCode() },
                    enabled = false
                )
            } else {
                FragmentButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.send_code_btn),
                    onClick = { vm.sendCode() }
                )
            }

            Label(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = PaddingSmall),
                text = stringResource(id = R.string.code_sent_message),
                style = MaterialTheme.typography.labelSmall,
                maxLines = 4
            )

            InputField(
                label = stringResource(id = R.string.code_lbl),
                value = uiState.code,
                onValueChange = { vm.updateCode(it) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                isError = uiState.codeError != null
            )

            uiState.codeError?.let {
                ErrorLabel(text = uiState.codeError!!)
            }

            FragmentButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.verify_code_btn),
                onClick = { vm.validateCode() }
            )
        } else {
            FragmentButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.send_code_btn),
                onClick = { vm.sendCode() }
            )
        }
    }
}



@Preview
@Composable
private fun ValidateCodeDialogPreview() {
    WorkoutTrackerTheme {
        ValidateCodeDialog(email = "", false, validateFor = ValidateCodeViewModel.ValidateFor.RESET_PASSWORD)
    }
}
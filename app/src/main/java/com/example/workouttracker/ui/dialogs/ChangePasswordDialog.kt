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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.R
import com.example.workouttracker.ui.extensions.customBorder
import com.example.workouttracker.ui.reusable.DialogButton
import com.example.workouttracker.ui.reusable.InputField
import com.example.workouttracker.ui.theme.DialogFooterSize
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.viewmodel.ChangePasswordViewModel
import androidx.compose.runtime.getValue
import com.example.workouttracker.ui.reusable.ErrorLabel

/**
 * Dialog to allow the user to change / reset password
 * @param email email value, if not empty the dialog is opened for password reset
 */
@Composable
fun ChangePasswordDialog(
    email: String = "",
    onReset: () -> Unit = {},
    vm: ChangePasswordViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        vm.resetState(
            emailVal = email,
            onReset = onReset
        )
    }

    val state by vm.uiState.collectAsStateWithLifecycle()
    val newPassFocusReq = remember { FocusRequester() }
    val confirmPassFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PaddingSmall)
    ) {

        if (state.email.isEmpty()) {
            InputField(
                modifier = Modifier.padding(horizontal = PaddingSmall),
                label = stringResource(id = R.string.old_password_lbl),
                value = state.oldPassword,
                onValueChange = { vm.updateOldPassword(it) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { newPassFocusReq.requestFocus()} ),
                isError = state.oldPasswordError != null
            )

            state.oldPasswordError?.let {
                ErrorLabel(
                    modifier = Modifier.padding(end = PaddingSmall),
                    text = state.oldPasswordError!!
                )
            }
        }

        InputField(
            modifier = Modifier
                .padding(horizontal = PaddingSmall)
                .focusRequester(newPassFocusReq),
            label = stringResource(id = R.string.new_password_lbl),
            value = state.newPassword,
            onValueChange = { vm.updateNewPassword(it) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { confirmPassFocusRequester.requestFocus()} ),
            isError = state.newPasswordError != null
        )

        state.newPasswordError?.let {
            ErrorLabel(
                modifier = Modifier.padding(end = PaddingSmall),
                text = state.newPasswordError!!
            )
        }

        InputField(
            modifier = Modifier
                .padding(horizontal = PaddingSmall)
                .focusRequester(confirmPassFocusRequester),
            label = stringResource(id = R.string.confirm_pass_lbl),
            value = state.confirmPassword,
            onValueChange = { vm.updateConfirmPassword(it) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() } ),
            isError = state.confirmPasswordError != null
        )

        state.confirmPasswordError?.let {
            ErrorLabel(
                modifier = Modifier.padding(end = PaddingSmall),
                text = state.confirmPasswordError!!
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
private fun ChangePasswordDialogPreview() {
    WorkoutTrackerTheme {
        ChangePasswordDialog()
    }
}
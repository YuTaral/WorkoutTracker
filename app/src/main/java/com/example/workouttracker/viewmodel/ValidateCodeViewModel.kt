package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.R
import com.example.workouttracker.data.network.repositories.UserRepository
import com.example.workouttracker.ui.dialogs.ChangePasswordDialog
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.ui.managers.VibrationManager
import com.example.workouttracker.utils.ResourceProvider
import com.example.workouttracker.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/** ValidateCodeViewModel to manage the state of validate code screen */
@HiltViewModel
class ValidateCodeViewModel @Inject constructor(
    var userRepository: UserRepository,
    private var resourceProvider: ResourceProvider,
    private val vibrationManager: VibrationManager,
    private val dialogManager: DialogManager
): ViewModel() {

    /** Job to refresh count 60 seconds after code sent */
    private var resendCodeTimerJob: Job? = null

    /** Enum for different type of code validations */
    enum class ValidateFor {
        RESET_PASSWORD,
        CONFIRM_EMAIL
    }

    /** Register UI State representing the state of the Forgot password page */
    data class ValidateCodeUIState(
        val email: String = "",
        val code: String = "",
        val emailError: String? = null,
        val codeError: String? = null,

        val codeSent: Boolean = false,
        val codeValidated: Boolean = false,
        val resendTimer: Int = 0
    )

    /** UI state for the validate code dialog */
    private val _validateCodeUIState = MutableStateFlow(ValidateCodeUIState())
    val validateCodeUIState = _validateCodeUIState.asStateFlow()

    /** The type of validation */
    private var validateFor: ValidateFor = ValidateFor.RESET_PASSWORD

    /** Update the email */
    fun updateEmail(value: String) {
        _validateCodeUIState.update { it.copy(email = value) }
    }

    /** Update the email error */
    fun updateEmailError(value: String?) {
        _validateCodeUIState.update { it.copy(emailError = value) }
    }

    /** Update the code */
    fun updateCode(value: String) {
        _validateCodeUIState.update { it.copy(code = value) }
    }

    /** Update the code error */
    fun updateCodeError(value: String?) {
        _validateCodeUIState.update { it.copy(codeError = value) }
    }

    /** Update the code sent boolean */
    fun updateCodeSent(value: Boolean) {
        _validateCodeUIState.update { it.copy(codeSent = value) }

        if (value) {
            _validateCodeUIState.update { it.copy(resendTimer = 60) }

            resendCodeTimerJob = viewModelScope.launch {
                while (isActive) {
                    val newVal = _validateCodeUIState.value.resendTimer - 1

                    if (newVal == 0) {
                        _validateCodeUIState.update { it.copy(resendTimer = 0) }
                        resendCodeTimerJob?.cancel()
                    } else {
                        _validateCodeUIState.update { it.copy(resendTimer = newVal) }
                        delay(1000L)
                    }
                }
            }
        } else {
            _validateCodeUIState.update { it.copy(resendTimer = 0) }
            resendCodeTimerJob?.cancel()
        }
    }

    /**
     * Initialize the view model with the initial data
     * @param email the email to send the code to, may be empty
     * @param codeSent whether the code has been sent or not
     * @param validateForValue the type of validation
     */
    fun initialize(email: String, codeSent: Boolean, validateForValue: ValidateFor) {
        validateFor = validateForValue
        updateEmail(email)
        updateEmailError(null)
        updateCodeSent(value = codeSent)
        updateCode("")
        updateCodeError(null)
    }

    /** Send code to the enter emails */
    fun sendCode() {
        val state = _validateCodeUIState.value

        if (state.codeSent && state.resendTimer > 0) {
            return
        }

        if (!Utils.isValidEmail(state.email)) {
            updateEmailError(resourceProvider.getString(R.string.invalid_email_format))
            viewModelScope.launch { vibrationManager.makeVibration() }
            return
        } else {
            updateEmailError(null)
        }

        viewModelScope.launch(Dispatchers.IO) {
            userRepository.sendCode(
                email = state.email,
                onSuccess = {
                    updateCodeSent(true)
                }
            )
        }
    }

    /** Validate the entered code and sent it to the server */
    fun validateCode() {
        if (_validateCodeUIState.value.code.length != 6) {
            updateCodeError(resourceProvider.getString(R.string.invalid_code_lbl))
            viewModelScope.launch { vibrationManager.makeVibration() }
            return
        } else {
            updateCodeError(null)
        }

        if (validateFor == ValidateFor.RESET_PASSWORD) {
            validateForResetPassword()
        } else {
            validateForConfirmEmail()
        }
    }

    /** Validate code for reset password */
    private fun validateForResetPassword() {
        val state = _validateCodeUIState.value

        viewModelScope.launch {
            userRepository.verifyCode(
                codeType = ValidateFor.RESET_PASSWORD.name,
                email = state.email,
                code = state.code,
                onSuccess = {
                    // Open dialog to enter new password
                    viewModelScope.launch {
                        dialogManager.showDialog(
                            title = resourceProvider.getString(R.string.reset_password),
                            dialogName = "ChangePasswordDialog",
                            content = {
                                ChangePasswordDialog(
                                    email = state.email,
                                    onReset = {
                                        viewModelScope.launch {
                                            dialogManager.hideDialog("ChangePasswordDialog")
                                            dialogManager.hideDialog("ValidateCodeDialog")
                                        }
                                    }
                                )
                            }
                        )
                    }
                }
            )
        }
    }

    /** Validate code for email confirmation */
    private fun validateForConfirmEmail() {
        val state = _validateCodeUIState.value

        viewModelScope.launch {
            userRepository.verifyCode(
                codeType = ValidateFor.CONFIRM_EMAIL.name,
                email = state.email,
                code = state.code,
                onSuccess = {
                    viewModelScope.launch {
                       dialogManager.hideDialog("ValidateCodeDialog")
                    }
                }
            )
        }
    }
}
package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.R
import com.example.workouttracker.data.network.repositories.UserRepository
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.ui.managers.VibrationManager
import com.example.workouttracker.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject



/** Change password view model to control the UI state of change password dialog */
@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private var userRepository: UserRepository,
    private var resourceProvider: ResourceProvider,
    private val vibrationManager: VibrationManager,
    private val dialogManager: DialogManager
): ViewModel() {

    /** Login UI State representing the state of the Login page */
    data class UIState(
        val oldPassword: String = "",
        val newPassword: String = "",
        val confirmPassword: String = "",

        val oldPasswordError: String? = null,
        val newPasswordError: String? = null,
        val confirmPasswordError: String? = null,

        val email: String = ""
    )

    /** The UI state of the dialog */
    private val _uiState = MutableStateFlow(UIState())
    val uiState = _uiState.asStateFlow()

    /** Callback to execute when reset password has been successful */
    private lateinit var onResetCallback: () -> Unit

    init {
        resetState()
    }

    /**
     * Default the fields to empty
     * @param emailVal email value, if not empty the dialog is opened for password reset
     */
    fun resetState(emailVal: String = "", onReset: () -> Unit = {}) {
        _uiState.value = UIState()
        _uiState.update { it.copy(email = emailVal) }
        onResetCallback = onReset
    }

    /** Update the old password field in the login UI state */
    fun updateOldPassword(value: String) {
        _uiState.update { it.copy(oldPassword = value) }
    }

    /** Update the new password field in the login UI state */
    fun updateNewPassword(value: String) {
        _uiState.update { it.copy(newPassword = value) }
    }

    /** Update the confirm password field in the login UI state */
    fun updateConfirmPassword(value: String) {
        _uiState.update { it.copy(confirmPassword = value) }
    }

    /** Update the old password error field in the login UI state */
    private fun updateOldPasswordError(value: String?) {
        _uiState.update { it.copy(oldPasswordError = value) }
    }

    /** Update the new password error field in the login UI state */
    private fun updateNewPasswordError(value: String?) {
        _uiState.update { it.copy(newPasswordError = value) }
    }

    /** Update the confirm password error field in the login UI state */
    private fun updateConfirmPasswordError(value: String?) {
        _uiState.update { it.copy(confirmPasswordError = value) }
    }

    /** Save the password change*/
    fun save() {
        if (!validate()) {
            return
        }

        if (uiState.value.email.isEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                userRepository.changePassword(
                    oldPassword = uiState.value.oldPassword,
                    password = uiState.value.newPassword,
                    onSuccess = {
                        viewModelScope.launch {
                            dialogManager.hideDialog("ChangePasswordDialog")
                        }
                    }
                )
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                userRepository.resetPassword(
                    email = uiState.value.email,
                    password = uiState.value.newPassword,
                    onSuccess = {
                        viewModelScope.launch {
                            dialogManager.hideDialog("ChangePasswordDialog")
                            onResetCallback()
                        }
                    }
                )
            }
        }
    }

    /** Validate the ui state fields and return true/false */
    private fun validate(): Boolean {
        val state = uiState.value
        var success = true

        if (state.email.isEmpty()) {
            if (state.oldPassword.isEmpty()) {
                updateOldPasswordError(resourceProvider.getString(R.string.error_msg_blank_pass))
                viewModelScope.launch { vibrationManager.makeVibration() }
                success = false

            } else {
                updateOldPasswordError(null)
            }
        }

        if (state.newPassword.isEmpty()) {
            updateNewPasswordError(resourceProvider.getString(R.string.error_msg_blank_pass))
            viewModelScope.launch { vibrationManager.makeVibration() }
            success = false

        } else {
            updateNewPasswordError(null)
        }

        if (state.confirmPassword.isEmpty()) {
            updateConfirmPasswordError(resourceProvider.getString(R.string.error_msg_blank_pass))
            viewModelScope.launch { vibrationManager.makeVibration() }
            success = false

        } else {
            updateConfirmPasswordError(null)
        }

        if (state.confirmPassword != state.newPassword) {
            updateConfirmPasswordError(resourceProvider.getString(R.string.error_msg_pass_match))
            viewModelScope.launch { vibrationManager.makeVibration() }
            success = false
        } else {
            updateConfirmPasswordError(null)
        }

        if (state.oldPassword == state.newPassword) {
            updateNewPasswordError(resourceProvider.getString(R.string.error_msg_pass_matches))
            viewModelScope.launch { vibrationManager.makeVibration() }
            success = false
        } else {
            updateNewPasswordError(null)
        }

        return success
    }
}
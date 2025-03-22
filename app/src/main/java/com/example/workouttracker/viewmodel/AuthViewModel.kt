package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.R
import com.example.workouttracker.utils.ResourceProvider
import com.example.workouttracker.data.network.repositories.UserRepository
import com.example.workouttracker.utils.Utils
import com.example.workouttracker.ui.managers.VibrationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Login UI State representing the state of the Login page */
data class LoginUiState(
    val email: String = "",
    val password: String = "",

    val emailError: String? = null,
    val passwordError: String? = null
)

/** Register UI State representing the state of the Register page */
data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)

/** AuthViewModel to manage the state of AuthScreen Login or Register */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private var resourceProvider: ResourceProvider,
    private var userRepository: UserRepository
): ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState = _loginUiState.asStateFlow()

    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState = _registerUiState.asStateFlow()

    private val _registerSuccessEvent = MutableSharedFlow<Unit>()
    val registerSuccessEvent = _registerSuccessEvent.asSharedFlow()

    /** Update the login email error field in the login UI state */
    private fun updateLoginEmailError(value: String?) {
        _loginUiState.update { it.copy(emailError = value) }
    }

    /** Update the login password error field in the login UI state */
    private fun updateLoginPasswordError(value: String?) {
        _loginUiState.update { it.copy(passwordError = value) }
    }

    /** Update the register email error field in the register UI state */
    private fun updateRegisterEmailError(value: String?) {
        _registerUiState.update { it.copy(emailError = value) }
    }

    /** Update the register password error field in the register UI state */
    private fun updateRegisterPassError(value: String?) {
        _registerUiState.update { it.copy(passwordError = value) }
    }

    /** Update the login email field in the login UI state */
    fun updateLoginEmail(value: String) {
        _loginUiState.update { it.copy(email = value) }
    }

    /** Update the login password field in the login UI state */
    fun updateLoginPassword(value: String) {
        _loginUiState.update { it.copy(password = value) }
    }

    /** Update the register email field in the register UI state */
    fun updateRegisterEmail(value: String) {
        _registerUiState.update { it.copy(email = value) }
    }

    /** Update the register confirm password error field in the register UI state */
    private fun updateConfirmPassError(value: String?) {
        _registerUiState.update { it.copy(confirmPasswordError = value) }
    }

    /** Update the register password field in the register UI state */
    fun updateRegisterPassword(value: String) {
        _registerUiState.update { it.copy(password = value) }
    }

    /** Update the register confirm password field in the register UI state */
    fun updateConfirmPassword(value: String) {
        _registerUiState.update { it.copy(confirmPassword = value) }
    }

    /** Try to login user */
    fun login() {
        val state = _loginUiState.value

        if (!Utils.isValidEmail(state.email)) {
            updateLoginEmailError(resourceProvider.getString(R.string.invalid_email_format))
            viewModelScope.launch { VibrationManager.makeVibration() }
            return

        } else {
            updateLoginEmailError(null)
        }

        if (state.password.isEmpty()) {
            updateLoginPasswordError(resourceProvider.getString(R.string.blank_password))
            viewModelScope.launch { VibrationManager.makeVibration() }
            return
        } else {
            updateLoginPasswordError(null)
        }

        viewModelScope.launch(Dispatchers.IO) {
            userRepository.login(email = state.email, password = state.password)
        }
    }

    /** Try to register user */
    fun register() {
        val state = _registerUiState.value

        if (!Utils.isValidEmail(state.email)) {
            updateRegisterEmailError(resourceProvider.getString(R.string.invalid_email_format))
            viewModelScope.launch { VibrationManager.makeVibration() }
            return
        } else {
            updateRegisterEmailError(null)
        }

        if (state.password.isEmpty()) {
            updateRegisterPassError(resourceProvider.getString(R.string.blank_password))
            viewModelScope.launch { VibrationManager.makeVibration() }
            return
        } else {
            updateRegisterPassError(null)
        }

        if (state.confirmPassword != state.password) {
            updateConfirmPassError(resourceProvider.getString(R.string.passwords_do_not_match))
            viewModelScope.launch { VibrationManager.makeVibration() }
            return
        } else {
            updateConfirmPassError(null)
        }

        viewModelScope.launch(Dispatchers.IO) {
            userRepository.register(email = state.email, password = state.password, onSuccess = {
                updateRegisterEmail("")
                updateRegisterPassword("")
                updateConfirmPassword("")

                viewModelScope.launch {
                    _registerSuccessEvent.emit(Unit)

                }
            })
        }
    }
}

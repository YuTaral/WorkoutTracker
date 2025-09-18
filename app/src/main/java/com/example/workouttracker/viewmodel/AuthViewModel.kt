package com.example.workouttracker.viewmodel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.R
import com.example.workouttracker.data.managers.SharedPrefsManager
import com.example.workouttracker.utils.ResourceProvider
import com.example.workouttracker.data.network.repositories.UserRepository
import com.example.workouttracker.utils.Utils
import com.example.workouttracker.ui.managers.VibrationManager
import androidx.credentials.CredentialManager
import com.example.workouttracker.data.network.repositories.SystemLogRepository
import com.example.workouttracker.ui.dialogs.ChangePasswordDialog
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.ui.managers.SnackbarManager
import com.example.workouttracker.utils.Constants
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** AuthViewModel to manage the state of AuthScreen Login or Register */
@HiltViewModel
class AuthViewModel @Inject constructor(
    var userRepository: UserRepository,
    private var systemLogRepository: SystemLogRepository,
    private var resourceProvider: ResourceProvider,
    private var sharedPrefsManager: SharedPrefsManager,
    private val vibrationManager: VibrationManager,
    private val snackbarManager: SnackbarManager,
    private val dialogManager: DialogManager
): ViewModel() {

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

    /** Register UI State representing the state of the Forgot password page */
    data class ForgotPasswordUiState(
        val email: String = "",
        val code: String = "",
        val emailError: String? = null,
        val codeError: String? = null,

        val codeSent: Boolean = false,
        val codeValidated: Boolean = false,
        val resendTimer: Int = 0
    )

    /** Track when the token has been validated */
    private val _tokenValidated = MutableStateFlow(false)
    val tokenValidated = _tokenValidated.asStateFlow()

    /** Job to refresh count 60 seconds after code sent */
    private var resendCodeTimerJob: Job? = null

    init {
        checkAutoLogin()
    }

    /** UI state for the login screen */
    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState = _loginUiState.asStateFlow()

    /** UI state for the register screen */
    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState = _registerUiState.asStateFlow()

    /** UI state for the forgot password screen */
    private val _forgotPasswordUiState = MutableStateFlow(ForgotPasswordUiState())
    val forgotPasswordUiState = _forgotPasswordUiState.asStateFlow()

    /** Event to notify that the registration was successful */
    private val _showLoginPageEvent = MutableSharedFlow<Unit>()
    val showLoginPageEvent = _showLoginPageEvent.asSharedFlow()

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

    /** Update the forgot password email field in the forgot password UI state */
    fun updateForgotPasswordEmail(value: String) {
        _forgotPasswordUiState.update { it.copy(email = value) }
    }

    /** Update the forgot password email error in the forgot password UI state */
    fun updateForgotPasswordEmailError(value: String?) {
        _forgotPasswordUiState.update { it.copy(emailError = value) }
    }

    /** Update the code in forgot password UI state */
    fun updateCode(value: String) {
        _forgotPasswordUiState.update { it.copy(code = value) }
    }

    /** Update the code error in the forgot password UI state */
    fun updateCodeError(value: String?) {
        _forgotPasswordUiState.update { it.copy(codeError = value) }
    }

    /** Update the code sent boolean in forgot password UI state */
    fun updateCodeSent(value: Boolean) {
        _forgotPasswordUiState.update { it.copy(codeSent = value) }

        if (value) {
            _forgotPasswordUiState.update { it.copy(resendTimer = 60) }

            resendCodeTimerJob = viewModelScope.launch {
                while (isActive) {
                    val newVal = _forgotPasswordUiState.value.resendTimer - 1

                    if (newVal == 0) {
                        _forgotPasswordUiState.update { it.copy(resendTimer = 0) }
                        resendCodeTimerJob?.cancel()
                    } else {
                        _forgotPasswordUiState.update { it.copy(resendTimer = newVal) }
                        delay(1000L)
                    }
                }
            }
        } else {
            _forgotPasswordUiState.update { it.copy(resendTimer = 0) }
            resendCodeTimerJob?.cancel()
        }
    }

    /** Try to login user */
    fun login() {
        val state = _loginUiState.value

        if (!Utils.isValidEmail(state.email)) {
            updateLoginEmailError(resourceProvider.getString(R.string.invalid_email_format))
            viewModelScope.launch { vibrationManager.makeVibration() }
            return

        } else {
            updateLoginEmailError(null)
        }

        if (state.password.isEmpty()) {
            updateLoginPasswordError(resourceProvider.getString(R.string.blank_password))
            viewModelScope.launch { vibrationManager.makeVibration() }
            return
        } else {
            updateLoginPasswordError(null)
        }

        viewModelScope.launch(Dispatchers.IO) {
            userRepository.login(
                email = state.email,
                password = state.password,
                onSuccess = {
                    if (sharedPrefsManager.isFirstAppStart()) {
                        viewModelScope.launch {
                            userRepository.requestAllPerm()
                        }
                        sharedPrefsManager.setFirstStartApp()
                    }
                }
            )
        }
    }

    /** Try to register user */
    fun register() {
        val state = _registerUiState.value

        if (!Utils.isValidEmail(state.email)) {
            updateRegisterEmailError(resourceProvider.getString(R.string.invalid_email_format))
            viewModelScope.launch { vibrationManager.makeVibration() }
            return
        } else {
            updateRegisterEmailError(null)
        }

        if (state.password.isEmpty()) {
            updateRegisterPassError(resourceProvider.getString(R.string.blank_password))
            viewModelScope.launch { vibrationManager.makeVibration() }
            return
        } else {
            updateRegisterPassError(null)
        }

        if (state.confirmPassword != state.password) {
            updateConfirmPassError(resourceProvider.getString(R.string.passwords_do_not_match))
            viewModelScope.launch { vibrationManager.makeVibration() }
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
                    updateLoginEmail(state.email)
                    updateLoginPassword("")
                    _showLoginPageEvent.emit(Unit)
                }
            })
        }
    }

    /** Send code to the enter emails */
    fun sendCode() {
        val state = _forgotPasswordUiState.value

        if (state.codeSent && state.resendTimer > 0) {
            return
        }

        if (!Utils.isValidEmail(state.email)) {
            updateForgotPasswordEmailError(resourceProvider.getString(R.string.invalid_email_format))
            viewModelScope.launch { vibrationManager.makeVibration() }
            return
        } else {
            updateForgotPasswordEmailError(null)
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
        val state = _forgotPasswordUiState.value

        if (state.code.length != 6) {
            updateCodeError(resourceProvider.getString(R.string.invalid_code_lbl))
            viewModelScope.launch { vibrationManager.makeVibration() }
            return
        } else {
            updateCodeError(null)
        }

        viewModelScope.launch {
            userRepository.verifyCode(
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
                                            _showLoginPageEvent.emit(Unit)
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

    /** Performs a check whether there is stored user and valid token and if so auto login the user */
    private fun checkAutoLogin() {
        val token = sharedPrefsManager.getStoredToken()
        val userModel = sharedPrefsManager.getStoredUser()

        if (userModel != null && token.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                userRepository.validateToken(
                    token = token,
                    onSuccess = {
                        userRepository.updateUser(userModel)
                        _tokenValidated.value = true
                    },
                    onFailure = {
                        _tokenValidated.value = true
                    }
                )
            }
        } else {
            _tokenValidated.value = true
        }
    }

    /** Start the Google Sign-In prompt */
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun startGoogleSignIn(context: Context) {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .setServerClientId(Constants.GOOGLE_WEB_CLIENT_ID)
            .setNonce(generateNonce())
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        viewModelScope.launch {
            try {
                val result = CredentialManager.create(context).getCredential(
                    request = request,
                    context = context,
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                // Handle failure
                Log.e("GoogleSignIn", "Google Sign-In failed", e)
                viewModelScope.launch(Dispatchers.IO) {
                    systemLogRepository.addSystemLog(
                        message = "Google Sign-In failed: ${e.errorMessage}",
                        stackTrace = e.stackTraceToString()
                    )
                }
                snackbarManager.showSnackbar(resourceProvider.getString(R.string.google_sign_in_error))
                vibrationManager.makeVibration()
            }
        }
    }

    /**
     * Handle the sign-in result
     * @param result The GetCredentialResponse containing the credential information
     */
    private fun handleSignIn(result: GetCredentialResponse) {
        val credential = result.credential

        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                viewModelScope.launch(Dispatchers.IO) {
                    userRepository.googleSignIn(
                        idToken = googleIdTokenCredential.idToken,
                        onSuccess = {
                            if (sharedPrefsManager.isFirstAppStart()) {
                                viewModelScope.launch {
                                    userRepository.requestAllPerm()
                                }
                                sharedPrefsManager.setFirstStartApp()
                            }
                        }
                    )
                }
            } catch (_: GoogleIdTokenParsingException) {
                viewModelScope.launch {
                    snackbarManager.showSnackbar(resourceProvider.getString(R.string.google_sign_in_error))
                }
            }
        } else {
            viewModelScope.launch {
                snackbarManager.showSnackbar(resourceProvider.getString(R.string.google_sign_in_error))
            }
        }
    }

    /** Generate a random nonce of specified length */
    private fun generateNonce(length: Int = 32): String {
        val charset = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }
}

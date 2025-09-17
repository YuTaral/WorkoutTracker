package com.example.workouttracker.ui.screens

import android.content.Context
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.R
import com.example.workouttracker.ui.reusable.FragmentButton
import com.example.workouttracker.ui.reusable.ErrorLabel
import com.example.workouttracker.ui.reusable.InputField
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.*
import com.example.workouttracker.viewmodel.AuthViewModel
import com.example.workouttracker.viewmodel.AuthViewModel.ForgotPasswordUiState
import com.example.workouttracker.viewmodel.AuthViewModel.LoginUiState
import com.example.workouttracker.viewmodel.AuthViewModel.RegisterUiState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** Different pages in the authentication screen */
enum class Page {
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD,
}

/** The authentication screen which contains login / register pages */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun AuthScreen(vm: AuthViewModel = hiltViewModel()) {
    val pagerState = rememberPagerState(initialPage = Page.LOGIN.ordinal)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        vm.showLoginPageEvent.collect {
            coroutineScope.launch {
                pagerState.animateScrollToPage(Page.LOGIN.ordinal)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .navigationBarsPadding()
            .padding(top = PaddingSmall),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalPager(
            state = pagerState,
            count = 3,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                Page.LOGIN.ordinal -> {
                    vm.updateCodeSent(false)

                    LoginPage(
                        state = vm.loginUiState,
                        onEmailChange = { vm.updateLoginEmail(it) },
                        onPasswordChange = { vm.updateLoginPassword(it) },
                        onSwitchClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(Page.REGISTER.ordinal)
                            }
                        },
                        onLoginClick = { vm.login() },
                        onGoogleSignInClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                vm.startGoogleLogIn(it)
                            }
                        },
                        onForgotPasswordClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(Page.FORGOT_PASSWORD.ordinal)
                            }
                        }
                    )
                }

                Page.REGISTER.ordinal -> {
                    vm.updateCodeSent(false)

                    RegisterPage(
                        state = vm.registerUiState,
                        onEmailChange = { vm.updateRegisterEmail(it) },
                        onPasswordChange = { vm.updateRegisterPassword(it) },
                        onConfirmPasswordChange = { vm.updateConfirmPassword(it) },
                        onSwitchClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(Page.LOGIN.ordinal)
                            }
                        },
                        onRegisterClick = { vm.register() },
                        onGoogleSignInClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                vm.startGoogleSignIn(it)
                            }
                        }
                    )
                }

                Page.FORGOT_PASSWORD.ordinal -> {
                    ForgotPasswordPage(
                        state = vm.forgotPasswordUiState,
                        onEmailChange = { vm.updateForgotPasswordEmail(it) },
                        onSendCodeClick = { vm.sendCode() },
                        onCodeChange = { vm.updateCode(it) },
                        onValidateCodeClick = { vm.validateCode() }
                    )
                }
            }
        }
    }
}

/** Login Page */
@Composable
private fun LoginPage(
    state: StateFlow<LoginUiState>,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSwitchClick: () -> Unit,
    onLoginClick: () -> Unit,
    onGoogleSignInClick: (Context) -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    val uiState by state.collectAsStateWithLifecycle()

    AuthForm(
        title = stringResource(id = R.string.login_lbl),
        email = uiState.email,
        emailError = uiState.emailError,
        password = uiState.password,
        passwordError = uiState.passwordError,
        confirmPassword = null,
        confirmPasswordError = null,
        onEmailChange = onEmailChange,
        onPasswordChange = onPasswordChange,
        onConfirmPasswordChange = null,
        switchText = stringResource(id = R.string.click_for_register_lbl),
        onSwitchClick = onSwitchClick,
        buttonText = stringResource(id = R.string.login_btn),
        onButtonClick = onLoginClick,
        googleSignIn = onGoogleSignInClick,
        onForgotPasswordClick = onForgotPasswordClick
    )
}

/** Register Page */
@Composable
private fun RegisterPage(
    state: StateFlow<RegisterUiState>,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSwitchClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onGoogleSignInClick: (Context) -> Unit
) {
    val uiState by state.collectAsStateWithLifecycle()

    AuthForm(
        title = stringResource(id = R.string.register_lbl),
        email = uiState.email,
        emailError = uiState.emailError,
        password = uiState.password,
        passwordError = uiState.passwordError,
        confirmPassword = uiState.confirmPassword,
        confirmPasswordError = uiState.confirmPasswordError,
        onEmailChange = onEmailChange,
        onPasswordChange = onPasswordChange,
        onConfirmPasswordChange = onConfirmPasswordChange,
        switchText = stringResource(id = R.string.click_for_login_lbl),
        onSwitchClick = onSwitchClick,
        buttonText = stringResource(id = R.string.register_lbl),
        onButtonClick = onRegisterClick,
        googleSignIn = onGoogleSignInClick
    )
}

/** Common Authentication Form */
@Composable
private fun AuthForm(
    title: String,
    email: String,
    emailError: String?,
    password: String,
    passwordError: String?,
    confirmPassword: String?,
    confirmPasswordError: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: ((String) -> Unit)?,
    switchText: String,
    onSwitchClick: () -> Unit,
    buttonText: String,
    onButtonClick: () -> Unit,
    googleSignIn: (Context) -> Unit,
    onForgotPasswordClick: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val passwordFocusRequester = remember { FocusRequester() }
    val confirmPassFocusRequester = remember { FocusRequester() }
    var passwordKeyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
    var passwordKeyboardActions = KeyboardActions(onNext = {confirmPassFocusRequester.requestFocus()})

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = PaddingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PaddingSmall)
    ) {
        Label(text = title, style = MaterialTheme.typography.titleLarge)

        InputField(
            label = stringResource(id = R.string.email_lbl),
            value = email,
            onValueChange = onEmailChange,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { passwordFocusRequester.requestFocus() }),
            isError = emailError != null
        )

        emailError?.let {
            ErrorLabel(text = emailError)
        }

        if (confirmPassword == null) {
            // Change the password option and action if there is no confirm password
            passwordKeyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
            passwordKeyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
        }

        InputField(
            modifier = Modifier.focusRequester(passwordFocusRequester),
            label = stringResource(id = R.string.password_lbl),
            value = password,
            onValueChange = onPasswordChange,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = passwordKeyboardOptions,
            keyboardActions = passwordKeyboardActions,
            isError = passwordError != null
        )

        passwordError?.let {
            ErrorLabel(text = passwordError)
        }

        confirmPassword?.let {
            InputField(
                modifier = Modifier.focusRequester(confirmPassFocusRequester),
                label = stringResource(id = R.string.confirm_pass_lbl),
                value = it,
                onValueChange = onConfirmPasswordChange!!,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                isError = confirmPasswordError != null
            )

            confirmPasswordError?.let {
                ErrorLabel(text = it)
            }
        }

        FragmentButton(
            modifier = Modifier.fillMaxWidth(),
            text = buttonText,
            onClick = onButtonClick
        )

        if (confirmPassword == null) {
            SwitchModeLabel(text = stringResource(
                id = R.string.forgot_password_lbl),
                onClick = { onForgotPasswordClick() }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            var ctx = LocalContext.current

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ColorAccent, CircleShape)
                    .padding(10.dp)
                    .clickable { googleSignIn(ctx) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon_google_sign_in),
                    contentDescription = "Google Sign In",
                    modifier = Modifier.size(24.dp)
                )

                Label(
                    modifier = Modifier.padding(start = PaddingMedium),
                    text = if (confirmPassword == null) {
                        stringResource(id = R.string.login_google_lbl)
                    } else {
                        stringResource(id = R.string.sign_in_google_lbl)
                    },
                )
            }
        }

        SwitchModeLabel(text = switchText, onClick = onSwitchClick)
    }
}

/** Forgot Password Page */
@Composable
private fun ForgotPasswordPage(
    state: StateFlow<ForgotPasswordUiState>,
    onEmailChange: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    onSendCodeClick: () -> Unit,
    onValidateCodeClick: () -> Unit,
) {
    val uiState by state.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = PaddingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PaddingSmall)
    ) {
        Label(text = stringResource(R.string.forgot_password_lbl), style = MaterialTheme.typography.titleLarge)

        InputField(
            label = stringResource(id = R.string.email_lbl),
            value = uiState.email,
            onValueChange = onEmailChange,
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
                    onClick = onSendCodeClick,
                    enabled = false
                )
            } else {
                FragmentButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.send_code_btn),
                    onClick = onSendCodeClick
                )
            }

            Label(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = PaddingSmall),
                text = stringResource(id = R.string.code_sent_message),
                maxLines = 3
            )

            InputField(
                label = stringResource(id = R.string.code_lbl),
                value = uiState.code,
                onValueChange = onCodeChange,
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
                onClick = onValidateCodeClick
            )
        } else {
            FragmentButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.send_code_btn),
                onClick = onSendCodeClick
            )
        }
    }
}

/** Switch Mode Label */
@Composable
fun SwitchModeLabel(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .border(1.dp, ColorAccent, CircleShape)
            .padding(10.dp)
            .clickable { onClick() }
    ) {
        Label(
            modifier = Modifier.fillMaxWidth(),
            text = text
        )
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
private fun DefaultPreview() {
    WorkoutTrackerTheme {
        AuthScreen()
    }
}

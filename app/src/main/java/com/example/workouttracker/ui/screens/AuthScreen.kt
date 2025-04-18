package com.example.workouttracker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.R
import com.example.workouttracker.ui.components.reusable.FragmentButton
import com.example.workouttracker.ui.components.reusable.ErrorLabel
import com.example.workouttracker.ui.components.reusable.InputField
import com.example.workouttracker.ui.components.reusable.Label
import com.example.workouttracker.ui.theme.*
import com.example.workouttracker.viewmodel.LoginUiState
import com.example.workouttracker.viewmodel.AuthViewModel
import com.example.workouttracker.viewmodel.RegisterUiState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class Page {
    LOGIN,
    REGISTER
}

/** The authentication screen which contains login / register pages */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun AuthScreen(vm: AuthViewModel = hiltViewModel()) {
    val pagerState = rememberPagerState(initialPage = Page.LOGIN.ordinal)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        vm.registerSuccessEvent.collect {
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
        AppLogo()

        HorizontalPager(
            state = pagerState,
            count = 2,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                Page.LOGIN.ordinal -> LoginPage(
                    state = vm.loginUiState,
                    onEmailChange = { vm.updateLoginEmail(it) },
                    onPasswordChange = { vm.updateLoginPassword(it) },
                    onSwitchClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(Page.REGISTER.ordinal)
                        }
                    },
                    onLoginClick = { vm.login() }
                )

                Page.REGISTER.ordinal -> RegisterPage(
                    state = vm.registerUiState,
                    onEmailChange = { vm.updateRegisterEmail(it) },
                    onPasswordChange = { vm.updateRegisterPassword(it) },
                    onConfirmPasswordChange = { vm.updateConfirmPassword(it) },
                    onSwitchClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(Page.LOGIN.ordinal)
                        }
                    },
                    onRegisterClick = { vm.register() }
                )
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
    onLoginClick: () -> Unit
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
        onButtonClick = onLoginClick
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
    onRegisterClick: () -> Unit
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
        onButtonClick = onRegisterClick
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
    onButtonClick: () -> Unit
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
                ErrorLabel(text = confirmPasswordError)
            }
        }

        SwitchModeLabel(text = switchText, onClick = onSwitchClick)

        Spacer(modifier = Modifier.weight(1f))

        FragmentButton(text = buttonText, onClick = onButtonClick)
    }
}

/** App Logo */
@Composable
fun AppLogo() {
    Image(
        painter = painterResource(id = R.drawable.icon_app_logo),
        contentDescription = "App logo",
        modifier = Modifier.size(100.dp)
    )
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
            text = text,
            style = LabelMediumGrey
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

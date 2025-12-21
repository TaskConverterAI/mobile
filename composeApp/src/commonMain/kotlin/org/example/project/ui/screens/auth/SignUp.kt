package org.example.project.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import org.example.project.ui.theme.TaskConvertAIAppTheme
import org.example.project.ui.theme.provideInterFontFamily

@Composable
fun RegistrationScreen(
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory),
    onMoveToSignInClicked: () -> Unit,
    onSuccessSignUp: () -> Unit
) {
    val signUpUiState by authViewModel.signUpUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    if (signUpUiState.state == 1) {
        onSuccessSignUp()
    }

    // Ошибка (показываем короткий тост/снэкбар снизу)
    LaunchedEffect(signUpUiState.state) {
        // Показываем тост только если была попытка (валидация) и неуспех
        if (signUpUiState.state == 0 && (signUpUiState.username.isNotEmpty() || signUpUiState.email.isNotEmpty())) {
            snackbarHostState.showSnackbar(
                message = "Ошибка регистрации. Проверьте введённые данные",
                withDismissAction = false,
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Surface(
            modifier = Modifier.fillMaxSize().padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            RegistrationContent(
                isLoginValid = signUpUiState.isUsernameCorrect,
                isEmailValid = signUpUiState.isEmailCorrect,
                isPasswordValid = signUpUiState.isPasswordCorrect,
                isConfirmPasswordValid = signUpUiState.isConfirmPasswordCorrect,
                onLoginChange = { login -> authViewModel.checkLogin(login) },
                onEmailChange = { email -> authViewModel.checkEmail(email) },
                onPasswordChange = { password -> authViewModel.checkPassword(password) },
                onConfirmPasswordChange = { confirm -> authViewModel.checkConfirmPassword(confirm) },
                login = signUpUiState.username,
                email = signUpUiState.email,
                password = signUpUiState.password,
                confirmPassword = signUpUiState.confirmPassword,
                {
                    authViewModel.signUp()
                },
                {
                    onMoveToSignInClicked()
                },
                loginErrMsg = signUpUiState.usernameErrMsg,
                emailErrMsg = signUpUiState.emailErrMsg,
                passwordErrMsg = signUpUiState.passwordErrMsg,
                confirmPasswordErrMsg = signUpUiState.confirmPasswordErrMsg
            )
        }
    }
}

@Composable
private fun RegistrationContent(
    isLoginValid: Boolean,
    isEmailValid: Boolean,
    isPasswordValid: Boolean,
    isConfirmPasswordValid: Boolean,
    onLoginChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    login: String,
    email: String,
    password: String,
    confirmPassword: String,
    onRegister: () -> Unit,
    onLogin: () -> Unit,
    loginErrMsg: String,
    emailErrMsg: String,
    passwordErrMsg: String,
    confirmPasswordErrMsg: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(bottom = 100.dp)
        ) {
            RegistrationHeader()

            RegistrationFormSection(
                login = login,
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                onLoginChange = onLoginChange,
                onEmailChange = onEmailChange,
                onPasswordChange = onPasswordChange,
                onConfirmPasswordChange = onConfirmPasswordChange,
                isLoginValid = isLoginValid,
                isEmailValid = isEmailValid,
                isPasswordValid = isPasswordValid,
                isConfirmPasswordValid = isConfirmPasswordValid,
                loginErrMsg = loginErrMsg,
                emailErrMsg = emailErrMsg,
                passwordErrMsg = passwordErrMsg,
                confirmPasswordErrMsg = confirmPasswordErrMsg
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            RegistrationButtonsSection(
                onRegister = onRegister,
                onLogin = onLogin
            )
        }
    }
}

@Composable
private fun RegistrationHeader() {
    Text(
        text = "Регистрация",
        style = TextStyle(
            fontFamily = provideInterFontFamily(),
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            lineHeight = 36.sp
        ),
        modifier = Modifier
            .padding(vertical = 20.dp)
    )
}

@Composable
private fun RegistrationFormSection(
    login: String,
    email: String,
    password: String,
    confirmPassword: String,
    onLoginChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    isLoginValid: Boolean,
    isEmailValid: Boolean,
    isPasswordValid: Boolean,
    isConfirmPasswordValid: Boolean,
    loginErrMsg: String,
    emailErrMsg: String,
    passwordErrMsg: String,
    confirmPasswordErrMsg: String
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        LabeledTextField(
            title = "Логин",
            value = login,
            hint = "Введите логин",
            onValueChange = onLoginChange,
            isValid = isLoginValid,
            errorMsg = loginErrMsg,
            onDone = {focusManager.moveFocus(FocusDirection.Down)}
        )

        LabeledTextField(
            title = "Почта",
            value = email,
            hint = "example@mail.com",
            onValueChange = onEmailChange,
            isValid = isEmailValid,
            errorMsg = emailErrMsg,
            onDone = {focusManager.moveFocus(FocusDirection.Down)}
        )

        LabeledTextField(
            title = "Пароль",
            value = password,
            hint = "Введите пароль",
            onValueChange = onPasswordChange,
            isPassword = true,
            isValid = isPasswordValid,
            errorMsg = passwordErrMsg,
            onDone = {focusManager.moveFocus(FocusDirection.Down)}
        )

        LabeledTextField(
            title = "Подтверждение пароля",
            value = confirmPassword,
            hint = "Введите пароль ещё раз",
            onValueChange = onConfirmPasswordChange,
            isPassword = true,
            isValid = isConfirmPasswordValid,
            errorMsg = confirmPasswordErrMsg,
            onDone = {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        )
        Spacer(modifier = Modifier.height(200.dp))
    }
}

@Composable
private fun LabeledTextField(
    title: String,
    value: String,
    hint: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
    isValid: Boolean,
    errorMsg: String,
    onDone: () -> Unit
) {
    val borderColor = if (isValid) {
        MaterialTheme.colorScheme.outline
    } else {
        MaterialTheme.colorScheme.error
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                focusedBorderColor = borderColor,
                unfocusedBorderColor = borderColor,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorSupportingTextColor = MaterialTheme.colorScheme.error
            ),
            isError = !isValid,
            supportingText = {
                if (!isValid) {
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {onDone()}
            ),
            singleLine = true
        )
    }
}

@Composable
private fun RegistrationButtonsSection(
    onRegister: () -> Unit,
    onLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RegisterButton(onRegister)
        LoginButton(onLogin)
    }
}

@Composable
private fun RegisterButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Зарегистрироваться",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.Center)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "next",
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun LoginButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = "Уже есть аккаунт? Войти",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun RegistrationPreview() {
    TaskConvertAIAppTheme {
        RegistrationScreen(onSuccessSignUp = {}, onMoveToSignInClicked = {})
    }
}

@Composable
private fun RegistrationPreviewWithErrors() {
    TaskConvertAIAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            RegistrationContent(
                isLoginValid = false,
                isEmailValid = false,
                isPasswordValid = false,
                isConfirmPasswordValid = false,
                {},
                {},
                {},
                {},
                "",
                "",
                "",
                "",
                {},
                {},
                "",
                "",
                "",
                ""
            )
        }
    }
}

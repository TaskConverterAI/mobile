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
fun EnterScreen(
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory),
    onMoveToSignUpClicked: () -> Unit,
    onSuccessSignIn: () -> Unit
) {
    val signInUiState by authViewModel.signInUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Успех
    if (signInUiState.state == 1) {
        onSuccessSignIn()
    }

    // Ошибка (показываем короткий тост/снэкбар снизу)
    LaunchedEffect(signInUiState.state) {
        if (signInUiState.state == 0 && signInUiState.username.isNotEmpty()) {
            snackbarHostState.showSnackbar(
                message = "Ошибка входа. Проверьте логин и пароль",
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
            EnterContent(
                signInUiState.username,
                signInUiState.password,
                { login -> authViewModel.updateLogin(login) },
                { password -> authViewModel.updatePassword(password) },
                onMoveToSignUpClicked,
                {
                    authViewModel.signIn()
                }
            )
        }
    }
}

@Composable
private fun EnterContent(
    login: String,
    password: String,
    onLoginChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegister: () -> Unit,
    onLogin: () -> Unit
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
            EnterHeader()

            EnterFormSection(
                login = login,
                password = password,
                onLoginChange = onLoginChange,
                onPasswordChange = onPasswordChange
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            EnterButtonsSection(
                onRegister = onRegister,
                onLogin = onLogin
            )
        }
    }
}

@Composable
private fun EnterHeader() {
    Text(
        text = "Вход",
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
private fun EnterFormSection(
    login: String,
    password: String,
    onLoginChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
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
            onDone = { focusManager.moveFocus(FocusDirection.Down) }
        )

        LabeledTextField(
            title = "Пароль",
            value = password,
            hint = "Введите пароль",
            onValueChange = onPasswordChange,
            isPassword = true,
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
    onDone: () -> Unit
) {
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
                unfocusedTextColor = MaterialTheme.colorScheme.primary
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    onDone()
                }
            ),
            singleLine = true
        )
    }
}

@Composable
private fun EnterButtonsSection(
    onRegister: () -> Unit,
    onLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LoginButton(onLogin)
        RegisterButton(onRegister)
    }
}

@Composable
private fun LoginButton(onClick: () -> Unit) {
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
                text = "Войти",
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
private fun RegisterButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = "Нет аккаунта? Зарегистрироваться",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun EnterPreview() {
    TaskConvertAIAppTheme {
        EnterScreen(onSuccessSignIn = {}, onMoveToSignUpClicked = {})
    }
}

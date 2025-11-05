package org.example.project.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import org.example.project.AppDependencies
import org.example.project.data.auth.AuthRepository

data class SignUpUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isUsernameCorrect: Boolean = true,
    val isEmailCorrect: Boolean = true,
    val isPasswordCorrect: Boolean = true,
    val isConfirmPasswordCorrect: Boolean = true,
    val usernameErrMsg: String = "",
    val emailErrMsg: String = "",
    val passwordErrMsg: String = "",
    val confirmPasswordErrMsg: String = "",
    val state: Int = 0
)

data class SignInUiState(
    val username: String = "",
    val password: String = "",
    val state: Int = 0
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _signUpUiState = MutableStateFlow(SignUpUiState())
    val signUpUiState: StateFlow<SignUpUiState> = _signUpUiState.asStateFlow()

    private val _signInUiState = MutableStateFlow(SignInUiState())
    val signInUiState: StateFlow<SignInUiState> = _signInUiState.asStateFlow()


    fun checkLogin(login: String) {
        var errorMsg = ""
        val isCorrectLength = login.length in 3..32

        if (!isCorrectLength) {
            errorMsg = "Имя должно иметь длину 3 - 32 символа"
        }

        val usernameRegex = Regex("^[a-zA-Z0-9]+$")
        val isCorrectSymbols = usernameRegex.matches(login)

        if (!isCorrectSymbols) {
            errorMsg = "Имя должно содержать только латинские буквы и цифры"
        }

        val isCorrect = isCorrectSymbols && isCorrectLength

        _signUpUiState.update { currentState ->
            currentState.copy(username = login, isUsernameCorrect = isCorrect, usernameErrMsg = errorMsg)
        }
    }

    fun checkEmail(email: String) {
        var errorMsg = ""
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        val isCorrect = email.isNotEmpty() && emailRegex.matches(email)

        if (!isCorrect) {
            errorMsg = "Некорректный почтовый адрес"
        }

        _signUpUiState.update { currentState ->
            currentState.copy(email = email, isEmailCorrect = isCorrect, emailErrMsg = errorMsg)
        }
    }

    fun checkPassword(password: String) {
        var errorMsg = ""
        val correctLength = password.length in 8..<256

        if (!correctLength) {
            errorMsg = "Пароль должен быть длиннее 7 символов"
        }

        val hasUpperCase = password.any { it.isUpperCase() }

        if (!hasUpperCase) {
            errorMsg = "Пароль должен содержать прописные буквы"
        }

        val hasLowerCase = password.any { it.isLowerCase() }

        if (!hasLowerCase) {
            errorMsg = "Пароль должен содержать строчные буквы"
        }

        val hasDigit = password.any { it.isDigit() }

        if (!hasDigit) {
            errorMsg = "Пароль должен содержать цифры"
        }

        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

        if (!hasSpecialChar) {
            errorMsg = "Пароль должен содержать спецсимволы"
        }

        val isCorrect = correctLength && hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar

        _signUpUiState.update { currentState ->
            currentState.copy(password = password, isPasswordCorrect = isCorrect, passwordErrMsg = errorMsg)
        }

        checkConfirmPassword(signUpUiState.value.confirmPassword)
    }

    fun checkConfirmPassword(confirmPassword: String) {
        var errorMsg = ""
        val isCorrect = confirmPassword.equals(signUpUiState.value.password)

        if (!isCorrect) {
            errorMsg = "Пароли не совпадают"
        }

        _signUpUiState.update { currentState ->
            currentState.copy(
                confirmPassword = confirmPassword,
                isConfirmPasswordCorrect = isCorrect,
                confirmPasswordErrMsg = errorMsg
            )
        }
    }

    fun updateLogin(login: String) {
        _signInUiState.update { currentState ->
            currentState.copy(username = login)
        }
    }

    fun updatePassword(password: String) {
        _signInUiState.update { currentState ->
            currentState.copy(password = password)
        }
    }

    fun validateSignUp() {
        viewModelScope.launch {
            val result = authRepository.signUp(
                _signUpUiState.value.username,
                _signUpUiState.value.email,
                _signUpUiState.value.password
            )

            if (result) {
                _signUpUiState.update { currentState ->
                    currentState.copy(state = 1)
                }
            } else {
                _signUpUiState.update { currentState ->
                    currentState.copy(state = 0)
                }
            }
        }
    }

    fun validateSignIn() {
        viewModelScope.launch {
            val result = authRepository.signIn(
                _signInUiState.value.username,
                _signInUiState.value.password
            )

            if (result) {
                _signInUiState.update { currentState ->
                    currentState.copy(state = 1)

                }
            } else {
                _signInUiState.update { currentState ->
                    currentState.copy(state = 0)
                }
            }
        }
    }

    fun signUp() {
        if (!_signUpUiState.value.isUsernameCorrect ||
            !_signUpUiState.value.isEmailCorrect ||
            !_signUpUiState.value.isPasswordCorrect ||
            !_signUpUiState.value.isConfirmPasswordCorrect
        )
            return
        validateSignUp()
    }

    fun signIn() {
        validateSignIn()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val authRepository = AppDependencies.container.authRepository
                AuthViewModel(authRepository = authRepository)
            }
        }
    }
}

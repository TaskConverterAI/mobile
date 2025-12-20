package org.example.project.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import org.example.project.AppDependencies
import org.example.project.data.auth.AuthRepository
import org.example.project.data.commonData.User

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
    val isLoginCorrect: Boolean = true,
    val isPasswordCorrect: Boolean = true,
    val loginErrMsg: String = "",
    val passwordErrMsg: String = "",
    val state: Int = 0
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _signUpUiState = MutableStateFlow(SignUpUiState())
    val signUpUiState: StateFlow<SignUpUiState> = _signUpUiState.asStateFlow()

    private val _signInUiState = MutableStateFlow(SignInUiState())
    val signInUiState: StateFlow<SignInUiState> = _signInUiState.asStateFlow()

     private var _userId: MutableStateFlow<Long> = MutableStateFlow(-1L)
    var userId: StateFlow<Long> = _userId.asStateFlow()

    private var _currentUser: MutableStateFlow<User?> = MutableStateFlow(null)
    var currentUser: StateFlow<User?> = _currentUser.asStateFlow()


    fun getUserIdByToken() {
        viewModelScope.launch {
            _userId.value = authRepository.getUserIdByToken()
        }
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val userId = authRepository.getUserIdByToken()
                val user = AppDependencies.container.userRepository.getUserById(userId)
                if (user != null) {
                    _currentUser.value = user
                }
            } catch (e: Exception) {
                Logger.e("AuthViewModel", e) { "Error loading user data" }
            }
        }
    }

    fun checkLogin(login: String): Boolean {
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
        return isCorrect
    }

    fun checkEmail(email: String): Boolean {
        var errorMsg = ""
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        val isCorrect = email.isNotEmpty() && emailRegex.matches(email)

        if (!isCorrect) {
            errorMsg = "Некорректный почтовый адрес"
        }

        _signUpUiState.update { currentState ->
            currentState.copy(email = email, isEmailCorrect = isCorrect, emailErrMsg = errorMsg)
        }

        return isCorrect
    }

    fun checkPassword(password: String): Boolean {
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

        return isCorrect
    }

    fun checkConfirmPassword(confirmPassword: String): Boolean {
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

        return isCorrect
    }

    fun checkPair(login: String?, password: String?, onlyError: Boolean = false): Boolean
    {
        val preparedLogin = login ?: _signInUiState.value.username
        val preparedPassword = password ?: _signInUiState.value.password

        var isLoginCorrect = true
        var isPasswordCorrect = true
        var loginErrMsg = ""
        var passwordErrMsg = ""

        if (preparedLogin.isEmpty()) {
            isLoginCorrect = false
            loginErrMsg = "Это поле не может быть пустым"
        }

        if (preparedPassword.isEmpty()) {
            isPasswordCorrect = false
            passwordErrMsg = "Пароль не может быть пустым"
        }

        if (!isLoginCorrect || !isPasswordCorrect || !onlyError) {
            _signInUiState.update { currentState ->
                currentState.copy(
                    password = preparedPassword,
                    username = preparedLogin,
                    isLoginCorrect = isLoginCorrect,
                    isPasswordCorrect = isPasswordCorrect,
                    loginErrMsg = loginErrMsg,
                    passwordErrMsg = passwordErrMsg
                )
            }
        }

        return isLoginCorrect && isPasswordCorrect
    }

    fun updateLogin(login: String) {
        checkPair(login, null)
    }

    fun updatePassword(password: String) {
        checkPair(null, password)
    }

    fun validateSignUp() {

        val c1 = checkLogin(_signUpUiState.value.username)
        val c2 = checkEmail(_signUpUiState.value.email)
        val c3 = checkPassword(_signUpUiState.value.password)
        val c4 = checkConfirmPassword(_signUpUiState.value.confirmPassword)

        if (!(c1 && c2 && c3 && c4))
            return

        viewModelScope.launch {
            val result = authRepository.signUp(
                _signUpUiState.value.username,
                _signUpUiState.value.email,
                _signUpUiState.value.password
            )

            if (result.success) {
                _signUpUiState.update { currentState ->
                    currentState.copy(state = 1)
                }
            } else {
                var isLoginCorrect: Boolean = true
                var isEmailCorrect: Boolean = true
                var loginErrMsg = ""
                var emailErrMsg = ""

                result.errors.forEach { err ->

                    if (err.contains("email")) {
                        isEmailCorrect = false
                        emailErrMsg = "Данная почта уже зарегистрирована"
                    }

                    if (err.contains("username")) {
                        isLoginCorrect = false
                        loginErrMsg = "Данный логин уже зарегистрирован"
                    }
                }

                _signUpUiState.update { currentState ->
                    currentState.copy(state = 0, isEmailCorrect = isEmailCorrect, isUsernameCorrect = isLoginCorrect, emailErrMsg = emailErrMsg, usernameErrMsg = loginErrMsg)
                }
            }
        }
    }

    fun validateSignIn() {
        if (!checkPair(null, null, true))
            return


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
                    currentState.copy(state = 0, isLoginCorrect = false, isPasswordCorrect = false, loginErrMsg = "", passwordErrMsg = "Неверный логин или пароль")
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

    fun logout(userId: Long) {
        _signInUiState.value = SignInUiState()
        _signUpUiState.value = SignUpUiState()
        viewModelScope.launch {
            authRepository.logout(userId)
        }
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

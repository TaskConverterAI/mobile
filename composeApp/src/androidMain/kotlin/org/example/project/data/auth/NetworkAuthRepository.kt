package org.example.project.data.auth

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody
import org.example.project.AppDependencies
import org.example.project.data.commonData.Privileges
import org.example.project.data.commonData.User
import org.example.project.model.DecodeAccessTokenRequest
import org.example.project.model.ErrorsResponse
import org.example.project.model.InvalidateSessionRequest
import org.example.project.model.RefreshAccessTokenRequest
import org.example.project.model.SignInUserRequest
import org.example.project.model.SignInUserResponse
import org.example.project.model.SignUpUserRequest
import org.example.project.model.SignUpUserResponse
import org.example.project.network.AuthApiService
import kotlin.RuntimeException

private var appContext: Context? = null

fun initAuthRepository(context: Context) {
    appContext = context.applicationContext
}
class NetworkAuthRepository(
    private val userAuthPreferencesRepository: UserAuthPreferencesRepository,
    private val authApiService: AuthApiService
): AuthRepository {
    private suspend fun parseAndSaveJWT(token: String, username: String, email: String) {
        try {
            val decodedJWT: DecodedJWT = JWT.decode(token)
            val userId = decodedJWT.getClaim("id").asLong()

            Log.i("MY_APP_TAG","userId is ${userId}, username: $username, email: $email")

            if (userId != null) {
                // Сохраняем userId в preferences
                userAuthPreferencesRepository.saveUserId(userId.toString())

                // Создаем объект User с данными, которые ввел пользователь
                val user = User(
                    id = userId,
                    email = email,
                    username = username,
                    privileges = Privileges.member // По умолчанию member
                )

                // Сохраняем в локальную базу данных через UserRepository
                AppDependencies.container.userRepository.insertUser(user)
                Log.i("MY_APP_TAG", "User saved to local database: $user")
            }
        } catch (e: Exception) {
            Log.e("MY_APP_TAG", "Ошибка при парсинге токена: ${e.message}", e)
        }
    }

    override suspend fun getUserIdByToken(): Long {

        val refreshRes = refresh()
        if (!refreshRes) {
            throw RuntimeException("Refresh error")
        }
        val userData = decode() ?: throw RuntimeException("Decode error")
        return userData.first

    }

    override suspend fun signUp(
        login: String,
        email: String,
        password: String
    ): AuthResult {
        return try {
            val response = authApiService.signUp(SignUpUserRequest(login, email, password))

            if (response.isSuccessful) {
                val signInResponse = response.body()
                userAuthPreferencesRepository.saveAccessToken(signInResponse!!.accessToken!!)
                userAuthPreferencesRepository.saveRefreshToken(signInResponse.refreshToken!!)
                // Сохраняем данные пользователя локально
                parseAndSaveJWT(signInResponse.accessToken, login, email)

                AuthResult(true, listOf())
            } else {
                val errors: MutableList<String> = mutableListOf()
                val errorsResponse = parseErrorResponse(response.errorBody())

                if (errorsResponse.error != null)
                    errors.add(errorsResponse.error)

                errorsResponse.errors?.forEach { err ->
                    errors.add(err)
                }

                AuthResult(false, errors)
            }
        } catch (e: Exception) {
            //Logger.i { e.message.toString() }
            AuthResult(false, listOf())
        }
    }

    override suspend fun signIn(
        login: String,
        password: String
    ): Boolean {
        return try {
            val response = authApiService.signIn(SignInUserRequest(login, password))

            if (response.isSuccessful) {
                val signInResponse = response.body()
                userAuthPreferencesRepository.saveAccessToken(signInResponse!!.accessToken)
                userAuthPreferencesRepository.saveRefreshToken(signInResponse.refreshToken)
                // При входе сохраняем login как username и email (так как login может быть и username, и email)
                parseAndSaveJWT(signInResponse.accessToken, login, login)
                true
            } else {
                val errorsResponse = parseErrorResponse(response.errorBody())
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun getUserId(): String {
        Log.i("MY_APP_TAG", userAuthPreferencesRepository.userId.first())
        return userAuthPreferencesRepository.userId.first()
    }

    override suspend fun refresh(): Boolean {
        return try {
            val token = userAuthPreferencesRepository.refreshToken.first()

            Log.d("TOKEN", token)

            val response = authApiService.refresh(RefreshAccessTokenRequest(token))

            if (response.isSuccessful) {
                userAuthPreferencesRepository.saveAccessToken(response.body()!!.accessToken)
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun decode(): Pair<Long, String>? {
        return try {
            val response = authApiService.decode(DecodeAccessTokenRequest(userAuthPreferencesRepository.accessToken.first()))

            if (response.isSuccessful) {
                Pair(response.body()!!.userId, response.body()!!.role)
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun logout(userId: Long): Boolean {
        return try {
            val response = authApiService.logout(InvalidateSessionRequest(userId))

            response.isSuccessful
        } catch (_: Exception) {
            false
        }
    }

    private fun parseErrorResponse(errorBody: ResponseBody?): ErrorsResponse {
        return try {
            val json = Json {ignoreUnknownKeys = true}
            errorBody?.let {
                val errorString = it.string()
                Log.i("MY_APP_TAG", errorString)
                val result = json.decodeFromString<ErrorsResponse>(errorString)
                Log.i("MY_APP_TAG", errorString)
                result
            } ?: ErrorsResponse(null, null, null)
        } catch (e: Exception) {
            ErrorsResponse(null, null, null)
        }
    }


    companion object {
        private var _instance: NetworkAuthRepository? = null

        fun getInstance(repo: UserAuthPreferencesRepository, authApiService: AuthApiService): NetworkAuthRepository {
            return _instance
                ?: NetworkAuthRepository(repo, authApiService).also { _instance = it }
        }
    }
}

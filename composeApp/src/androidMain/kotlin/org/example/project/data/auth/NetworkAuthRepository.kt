package org.example.project.data.auth

import android.util.Log
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.coroutines.flow.first
import org.example.project.model.SignInUserRequest
import org.example.project.model.SignUpUserRequest
import org.example.project.network.AuthApiService

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkAuthRepository(
    private val userAuthPreferencesRepository: UserAuthPreferencesRepository
): AuthRepository {
    private val baseAuthUrl = "http://192.168.31.79:8090/"
    private val authRetrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseAuthUrl)
        .build()

    private val authApiService: AuthApiService by lazy {
        authRetrofit.create(AuthApiService::class.java)
    }

    private suspend fun parseAndSaveJWT(token: String) {
        try {
            val decodedJWT: DecodedJWT = JWT.decode(token)
            val userId = decodedJWT.getClaim("id").asInt()?.toString()
            Log.i("MY_APP_TAG","userId is ${userId}")
            userAuthPreferencesRepository.saveUserId(userId ?: "")
        } catch (e: Exception) {
            println("Ошибка при парсинге токена: ${e.message}")
        }
    }

    override suspend fun signUp(
        login: String,
        email: String,
        password: String
    ): Boolean {
        return try {
            val response = authApiService.signUp(SignUpUserRequest(login, email, password))

            if (response.isSuccessful) {
                val signInResponse = response.body()
                userAuthPreferencesRepository.saveAccessToken(signInResponse!!.accessToken)
                userAuthPreferencesRepository.saveRefreshToken(signInResponse.refreshToken)
                parseAndSaveJWT(signInResponse.accessToken)
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
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
                parseAndSaveJWT(signInResponse.accessToken)
                true
            } else {
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
}

package org.example.project.data.auth

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.coroutines.flow.first
import org.example.project.model.DecodeAccessTokenRequest
import org.example.project.model.InvalidateSessionRequest
import org.example.project.model.RefreshAccessTokenRequest
import org.example.project.model.SignInUserRequest
import org.example.project.model.SignUpUserRequest
import org.example.project.network.AuthApiService

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.RuntimeException

class NetworkAuthRepository(
    private val userAuthPreferencesRepository: UserAuthPreferencesRepository,
    private val authApiService: AuthApiService
): AuthRepository {
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
        } catch (e: Exception) {
            //Logger.i { e.message.toString() }
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


    companion object {
        private var _instance: NetworkAuthRepository? = null

        fun getInstance(repo: UserAuthPreferencesRepository, authApiService: AuthApiService): NetworkAuthRepository {
            return _instance
                ?: NetworkAuthRepository(repo, authApiService).also { _instance = it }
        }
    }
}

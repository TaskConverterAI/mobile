package com.example.taskconvertaiapp.shared.data.auth

import com.example.taskconvertaiapp.shared.model.SignInUserRequest
import com.example.taskconvertaiapp.shared.model.SignUpUserRequest
import com.example.taskconvertaiapp.shared.network.AuthApiService

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
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }
}

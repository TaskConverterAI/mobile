package com.example.taskconvertaiapp.data.auth

import com.example.taskconvertaiapp.model.SignInUserRequest
import com.example.taskconvertaiapp.model.SignUpUserRequest
import com.example.taskconvertaiapp.network.AuthApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AuthRepository {

    suspend fun signUp(login: String, email: String, password: String): Boolean
    suspend fun signIn(login: String, password: String): Boolean

}


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

        try {
            val response =  authApiService.signUp(SignUpUserRequest(login, email, password))

            if (response.isSuccessful) {
                val signInResponse = response.body()
                userAuthPreferencesRepository.saveAccessToken(signInResponse!!.accessToken)
                userAuthPreferencesRepository.saveRefreshToken(signInResponse.refreshToken)
                return true
            } else {
                return false
            }

        } catch (e: Exception) {
            return false
        }
    }

    override suspend fun signIn(
        login: String,
        password: String
    ): Boolean {

        try {
            val response =  authApiService.signIn(SignInUserRequest(login, password))

            if (response.isSuccessful) {
                val signInResponse = response.body()
                userAuthPreferencesRepository.saveAccessToken(signInResponse!!.accessToken)
                userAuthPreferencesRepository.saveRefreshToken(signInResponse.refreshToken)
                return true
            } else {
                return false
            }

        } catch (e: Exception) {
            return false
        }
    }

}
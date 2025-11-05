package org.example.project.network

import org.example.project.model.SignInUserRequest
import org.example.project.model.SignInUserResponse
import org.example.project.model.SignUpUserRequest
import org.example.project.model.SignUpUserResponse

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/register")
    suspend fun signUp(@Body user: SignUpUserRequest): Response<SignUpUserResponse>

    @POST("auth/login")
    suspend fun signIn(@Body user: SignInUserRequest): Response<SignInUserResponse>
}

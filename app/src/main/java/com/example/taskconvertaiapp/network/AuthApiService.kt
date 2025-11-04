package com.example.taskconvertaiapp.network

import com.example.taskconvertaiapp.model.SignInUserRequest
import com.example.taskconvertaiapp.model.SignInUserResponse
import com.example.taskconvertaiapp.model.SignUpUserRequest
import com.example.taskconvertaiapp.model.SignUpUserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/register")
    suspend fun signUp(@Body user: SignUpUserRequest): Response<SignUpUserResponse>

    @POST("auth/login")
    suspend fun  signIn(@Body user: SignInUserRequest): Response<SignInUserResponse>
}
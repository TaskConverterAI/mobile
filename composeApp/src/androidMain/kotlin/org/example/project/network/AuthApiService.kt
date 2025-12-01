package org.example.project.network

import org.example.project.model.AccessTokenResponse
import org.example.project.model.DecodeAccessTokenRequest
import org.example.project.model.DecodedTokenResponse
import org.example.project.model.InvalidateSessionRequest
import org.example.project.model.RefreshAccessTokenRequest
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

    @POST("auth/refresh")
    suspend fun refresh(@Body user: RefreshAccessTokenRequest): Response<AccessTokenResponse>

    @POST("auth/logout")
    suspend fun logout(@Body user: InvalidateSessionRequest): Response<Unit>

    @POST("auth/decode")
    suspend fun decode(@Body user: DecodeAccessTokenRequest): Response<DecodedTokenResponse>
}

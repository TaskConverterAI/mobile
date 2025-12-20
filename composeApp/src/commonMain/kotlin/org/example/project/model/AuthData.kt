package org.example.project.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignUpUserRequest (
    @SerialName(value = "username")
    val username: String,
    @SerialName(value = "email")
    val email: String,
    @SerialName(value = "password")
    val password: String
)

@Serializable
data class SignUpUserResponse (
    @SerialName(value = "accessToken")
    val accessToken: String,
    @SerialName(value = "refreshToken")
    val refreshToken: String
)

@Serializable
data class SignInUserRequest (
    @SerialName(value = "usernameOrEmail")
    val usernameOrEmail: String,
    @SerialName(value = "password")
    val password: String
)

@Serializable
data class SignInUserResponse (
    @SerialName(value = "accessToken")
    val accessToken: String,
    @SerialName(value = "refreshToken")
    val refreshToken: String
)

@Serializable
data class DecodeAccessTokenRequest (
    @SerialName(value = "accessToken")
    val accessToken: String
)

@Serializable
data class DecodedTokenResponse (
    @SerialName(value = "userId")
    val userId: Long,
    @SerialName(value = "role")
    val role: String
)

@Serializable
data class RefreshAccessTokenRequest (
    @SerialName(value = "refreshToken")
    val refreshToken: String
)

@Serializable
data class AccessTokenResponse (
    @SerialName(value = "accessToken")
    val accessToken: String
)

@Serializable
data class InvalidateSessionRequest (
    @SerialName(value = "userId")
    val userId: Long
)

@Serializable
data class ErrorsResponse (
    @SerialName(value = "error")
    val error: String? = null,
    @SerialName(value = "errors")
    val errors: List<String>? = null,
    @SerialName(value = "timestamp")
    val timestamp: String? = null
)
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

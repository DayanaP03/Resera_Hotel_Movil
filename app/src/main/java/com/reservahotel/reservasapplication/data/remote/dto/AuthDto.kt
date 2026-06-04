package com.reservahotel.reservasapplication.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String,
)

data class TokenRefreshRequest(
    val refresh: String,
)

data class AuthResponseDto(
    val access:   String,
    val refresh:  String,
    @SerializedName("user_id")  val userId:  Int,
    val username: String,
    val email:    String,
    val rol:      String? = "usuario",
)

data class TokenRefreshResponseDto(
    val access:  String,
    val refresh: String?,
)

package com.reservahotel.reservasapplication.data.repository

import com.reservahotel.reservasapplication.data.local.TokenDataStore
import com.reservahotel.reservasapplication.data.remote.api.AuthApi
import com.reservahotel.reservasapplication.data.remote.dto.AuthResponseDto
import com.reservahotel.reservasapplication.data.remote.dto.LoginRequest
import com.reservahotel.reservasapplication.domain.model.Usuario
import com.reservahotel.reservasapplication.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenDataStore: TokenDataStore
) : AuthRepository {

    override suspend fun login(
        username: String,
        password: String
    ): Result<AuthResponseDto> = runCatching {

        println("email = $username")
        println("PASSWORD = $password")

        val response = api.login(
            LoginRequest(
                email = username,
                password = password
            )
        )

        println("CODE = ${response.code()}")
        println("BODY = ${response.errorBody()?.string()}")

        if (response.isSuccessful) {

            val body = response.body()
                ?: throw Exception("Respuesta vacía del servidor")

            tokenDataStore.saveTokens(
                body.access,
                body.refresh
            )

            tokenDataStore.saveUser(
                body.userId,
                body.username,
                body.email,
                body.rol ?: "usuario"
            )

            body

        } else {

            throw Exception(
                "Error de login: ${response.code()}"
            )
        }
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        tokenDataStore.clearSession()
    }

    override suspend fun getSession(): Result<Usuario?> = runCatching {

        val snapshot = tokenDataStore.userSnapshot.first()

        snapshot?.let {
            Usuario(
                id = it.id,
                username = it.username,
                email = it.email,
                rol = it.rol
            )
        }
    }
}
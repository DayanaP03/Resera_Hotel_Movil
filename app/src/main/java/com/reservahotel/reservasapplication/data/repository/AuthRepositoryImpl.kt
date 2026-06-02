package com.reservahotel.reservasapplication.data.repository

import com.reservahotel.reservasapplication.data.local.TokenDataStore
import com.reservahotel.reservasapplication.data.remote.api.AuthApi
import com.reservahotel.reservasapplication.data.remote.dto.LoginRequest
import com.reservahotel.reservasapplication.data.remote.dto.AuthResponseDto
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

    override suspend fun login(username: String, password: String): Result<AuthResponseDto> = runCatching {
        val response = api.login(LoginRequest(username, password))
        if (response.isSuccessful) {
            val body = response.body()!!
            tokenDataStore.saveTokens(body.access, body.refresh)
            tokenDataStore.saveUser(body.userId, body.username, body.email, body.rol)
            body
        } else {
            error("Error de login: ${response.code()}")
        }
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        tokenDataStore.clearSession()
    }

    override suspend fun getSession(): Result<Usuario?> = runCatching {
        val snapshot = tokenDataStore.userSnapshot.first()
        snapshot?.let {
            Usuario(it.id, it.username, it.email, it.rol)
        }
    }
}

package com.reservahotel.reservasapplication.data.repository

import android.util.Log
import com.reservahotel.reservasapplication.data.local.TokenDataStore
import com.reservahotel.reservasapplication.data.remote.api.AuthApi
import com.reservahotel.reservasapplication.data.remote.dto.AuthResponseDto
import com.reservahotel.reservasapplication.data.remote.dto.LoginRequest
import com.reservahotel.reservasapplication.data.remote.dto.RegistroRequest
import com.reservahotel.reservasapplication.domain.model.Usuario
import com.reservahotel.reservasapplication.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "AuthRepo"

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenDataStore: TokenDataStore,
) : AuthRepository {

    override suspend fun login(
        username: String,
        password: String,
    ): Result<AuthResponseDto> = runCatching {

        val response = api.login(LoginRequest(email = username, password = password))
        Log.d(TAG, "login() HTTP ${response.code()}")

        if (response.isSuccessful) {
            val body    = response.body() ?: throw Exception("Respuesta vacía del servidor")
            val userDto = body.resolvedUser()

            // "rol" es la fuente de verdad — el backend devuelve "administrador" o "cliente"
            val rol     = userDto.rol?.trim()?.lowercase() ?: "cliente"
            // isStaff lo derivamos del rol porque el backend no lo devuelve en el objeto user
            val isStaff = rol == "administrador"

            Log.d(TAG, "login() OK → id=${userDto.id} username=${userDto.username} " +
                    "email=${userDto.email} rol=$rol isStaff(derivado)=$isStaff")

            tokenDataStore.saveTokens(body.access ?: "", body.refresh ?: "")
            tokenDataStore.saveUser(
                id       = userDto.id ?: 0,
                username = userDto.username ?: "",
                email    = userDto.email ?: "",
                rol      = rol,
                isStaff  = isStaff,
            )

            body
        } else {
            val err = response.errorBody()?.string() ?: "sin detalle"
            Log.e(TAG, "login() FAIL HTTP ${response.code()} — $err")
            throw Exception("Error de login (${response.code()}): $err")
        }
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String,
        password2: String,
    ): Result<Unit> = runCatching {
        val response = api.register(
            RegistroRequest(
                username  = username,
                email     = email,
                password  = password,
                password2 = password2,
            )
        )
        if (!response.isSuccessful) {
            throw Exception("Error en registro (${response.code()}): ${response.errorBody()?.string()}")
        }
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        Log.d(TAG, "logout()")
        tokenDataStore.clearSession()
    }

    override suspend fun getSession(): Result<Usuario?> = runCatching {
        val snap = tokenDataStore.userSnapshot.first()
        Log.d(TAG, "getSession() → $snap")
        snap?.let {
            Usuario(
                id       = it.id,
                username = it.username,
                email    = it.email,
                rol      = it.rol,
                isStaff  = it.isStaff,
            )
        }
    }
}

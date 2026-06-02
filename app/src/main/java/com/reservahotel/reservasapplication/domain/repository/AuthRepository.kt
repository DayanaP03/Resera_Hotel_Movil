package com.reservahotel.reservasapplication.domain.repository

import com.reservahotel.reservasapplication.data.remote.dto.AuthResponseDto
import com.reservahotel.reservasapplication.domain.model.Usuario

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<AuthResponseDto>
    suspend fun logout(): Result<Unit>
    suspend fun getSession(): Result<Usuario?>
}

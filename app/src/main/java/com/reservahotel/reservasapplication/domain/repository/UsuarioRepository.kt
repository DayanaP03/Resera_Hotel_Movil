package com.reservahotel.reservasapplication.domain.repository

import com.reservahotel.reservasapplication.domain.model.Usuario

interface UsuarioRepository {
    suspend fun getUsuarios(): Result<List<Usuario>>
    suspend fun updateRol(id: Int, rol: String, isStaff: Boolean): Result<Usuario>
    suspend fun toggleActive(id: Int, isActive: Boolean): Result<Usuario>
}

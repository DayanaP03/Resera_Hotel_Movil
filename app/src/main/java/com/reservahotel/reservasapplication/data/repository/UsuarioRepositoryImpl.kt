package com.reservahotel.reservasapplication.data.repository

import com.reservahotel.reservasapplication.data.remote.api.AdminApi
import com.reservahotel.reservasapplication.data.remote.dto.toDomain
import com.reservahotel.reservasapplication.domain.model.Usuario
import com.reservahotel.reservasapplication.domain.repository.UsuarioRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsuarioRepositoryImpl @Inject constructor(
    private val api: AdminApi,
) : UsuarioRepository {

    override suspend fun getUsuarios(): Result<List<Usuario>> = runCatching {
        val response = api.listUsers()
        if (response.isSuccessful) {
            response.body()?.map { it.toDomain() } ?: emptyList()
        } else {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun updateRol(id: Int, rol: String, isStaff: Boolean): Result<Usuario> = runCatching {
        val body = mapOf<String, Any>("rol" to rol, "is_staff" to isStaff)
        val response = api.updateUser(id, body)
        if (response.isSuccessful) {
            response.body()?.toDomain() ?: throw Exception("Error al actualizar usuario")
        } else {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun toggleActive(id: Int, isActive: Boolean): Result<Usuario> = runCatching {
        val body = mapOf<String, Any>("is_active" to isActive)
        val response = api.updateUser(id, body)
        if (response.isSuccessful) {
            response.body()?.toDomain() ?: throw Exception("Error al cambiar estado")
        } else {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }
}

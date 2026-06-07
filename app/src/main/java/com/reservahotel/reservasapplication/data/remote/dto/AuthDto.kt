package com.reservahotel.reservasapplication.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String,
)

data class RegistroRequest(
    val username: String,
    val email: String,
    val password: String,
    val password2: String,
)

data class TokenRefreshRequest(
    val refresh: String,
)

/**
 * Objeto "user" anidado dentro de la respuesta de login.
 * El backend devuelve: { "access": "...", "refresh": "...", "user": { ... } }
 * NOTA: is_staff NO viene en el objeto user, solo viene el campo "rol"
 */
data class UserDto(
    val id: Int? = null,
    @SerializedName("cliente_id") val clienteId: Int? = null,
    val username: String? = null,
    val email: String? = null,
    val rol: String? = null,
    @SerializedName("is_active")  val isActive:  Boolean? = null,
    @SerializedName("is_staff")   val isStaff:   Boolean? = null,
    @SerializedName("created_at") val createdAt: String?  = null,
)

/**
 * Respuesta completa de POST auth/login/
 *
 * Estructura real del backend (confirmada):
 * {
 *   "refresh": "...",
 *   "access":  "...",
 *   "user": {
 *     "id": 1,
 *     "username": "admin",
 *     "email": "admin123@gmail.com",
 *     "rol": "administrador",       ← FUENTE DE VERDAD para el panel
 *     "is_active": true,
 *     "created_at": "..."
 *   }
 * }
 */
data class AuthResponseDto(
    val access:  String?  = null,
    val refresh: String?  = null,
    @SerializedName("cliente_id") val rootClienteId: Int? = null,
    val user:    UserDto? = null,
) {
    /** Intenta obtener el cliente_id de donde sea que venga */
    fun getFinalClienteId(): Int {
        return rootClienteId ?: user?.clienteId ?: 0
    }

    /** Normaliza por si los campos llegan en la raíz en vez del objeto user */
    fun resolvedUser(): UserDto = user ?: UserDto()
}

data class TokenRefreshResponseDto(
    val access:  String,
    val refresh: String?,
)

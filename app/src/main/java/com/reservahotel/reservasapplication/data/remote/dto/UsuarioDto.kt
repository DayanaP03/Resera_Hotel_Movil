package com.reservahotel.reservasapplication.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.reservahotel.reservasapplication.domain.model.Usuario

data class UsuarioDto(
    val id: Int,
    val username: String,
    val email: String,
    val rol: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("is_staff") val isStaff: Boolean,
    @SerializedName("created_at") val createdAt: String? = null,
)

fun UsuarioDto.toDomain() = Usuario(
    id = id,
    username = username,
    email = email,
    rol = rol,
    isActive = isActive,
    isStaff = isStaff,
    createdAt = createdAt ?: "",
)

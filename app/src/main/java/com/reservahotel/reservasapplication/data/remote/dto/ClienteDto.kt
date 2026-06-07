package com.reservahotel.reservasapplication.data.remote.dto

import com.reservahotel.reservasapplication.domain.model.Cliente

data class ClienteDto(
    val id: Int,
    val nombre: String,
    val telefono: String?,
    val direccion: String?,
    val correo: String,
)

data class ClienteRequestDto(
    val nombre: String,
    val telefono: String?,
    val direccion: String?,
    val correo: String,
)

fun ClienteDto.toDomain() = Cliente(
    id = id,
    nombre = nombre,
    telefono = telefono,
    direccion = direccion,
    correo = correo,
)

fun Cliente.toRequestDto() = ClienteRequestDto(
    nombre = nombre,
    telefono = telefono,
    direccion = direccion,
    correo = correo,
)

package com.reservahotel.reservasapplication.data.remote.dto

import com.reservahotel.reservasapplication.domain.model.Servicio

data class ServicioDto(
    val id: Int = 0,
    val nombre: String = "",
    val descripcion: String? = null,
    val precio: String = "0.00",
    val activo: Boolean = true,
)

fun ServicioDto.toDomain() = Servicio(
    id          = id,
    nombre      = nombre,
    descripcion = descripcion,
    precio      = precio,
    activo      = activo,
)

fun Servicio.toDto() = ServicioDto(
    id          = id,
    nombre      = nombre,
    descripcion = descripcion,
    precio      = precio,
    activo      = activo,
)

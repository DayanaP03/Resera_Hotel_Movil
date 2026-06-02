package com.reservahotel.reservasapplication.domain.model

data class Servicio(
    val id: Int,
    val nombre: String,
    val descripcion: String?,
    val precio: String,
    val activo: Boolean
)

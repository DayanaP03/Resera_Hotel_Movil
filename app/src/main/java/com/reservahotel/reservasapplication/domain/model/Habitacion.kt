package com.reservahotel.reservasapplication.domain.model

data class Habitacion(
    val id: Int,
    val numero: String,
    val tipo: String,
    val precio_noche: String,
    val estado: String,
    val descripcion: String?,
    val capacidad: Int
)

package com.reservahotel.reservasapplication.domain.model

data class Reserva(
    val id: Int,
    val cliente: Int,
    val habitacion: Int,
    val servicios: List<Int>,
    val fecha_entrada: String,
    val fecha_salida: String,
    val estado: String,
    val observaciones: String?
)

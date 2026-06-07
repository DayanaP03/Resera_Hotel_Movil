package com.reservahotel.reservasapplication.domain.model

data class Reserva(
    val id: Int,
    val clienteNombre: String,
    val habitacionNumero: String,
    val fechaEntrada: String,
    val fechaSalida: String,
    val noches: Int,
    val estado: String,
    val estadoDisplay: String,
    val total: String,
    val createdAt: String,
)

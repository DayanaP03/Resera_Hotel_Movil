package com.reservahotel.reservasapplication.domain.model

data class Factura(
    val id: Int,
    val reserva: Int,
    val total: String,
    val estado_pago: String
)

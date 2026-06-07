package com.reservahotel.reservasapplication.domain.model

data class Factura(
    val id: Int,
    val reserva: Int,
    val total: String,
    val fechaEmision: String,
    val estadoPago: String,
    val estadoDisplay: String,
    val notas: String?,
    val totalPagado: Double,
    val saldoPendiente: Double,
)

package com.reservahotel.reservasapplication.domain.model

data class Pago(
    val id: Int,
    val factura: Int,
    val metodo_pago: String,
    val monto: String,
    val referencia: String?
)

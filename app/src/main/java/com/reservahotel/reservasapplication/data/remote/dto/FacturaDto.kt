package com.reservahotel.reservasapplication.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.reservahotel.reservasapplication.domain.model.Factura

data class FacturaDto(
    val id: Int = 0,
    val reserva: Int = 0,
    val total: String = "0.00",
    @SerializedName("fecha_emision")   val fechaEmision:   String = "",
    @SerializedName("estado_pago")     val estadoPago:     String = "",
    @SerializedName("estado_display")  val estadoDisplay:  String = "",
    val notas: String? = null,
    @SerializedName("total_pagado")    val totalPagado:    Double = 0.0,
    @SerializedName("saldo_pendiente") val saldoPendiente: Double = 0.0,
)

fun FacturaDto.toDomain() = Factura(
    id              = id,
    reserva         = reserva,
    total           = total,
    fechaEmision    = fechaEmision,
    estadoPago      = estadoPago,
    estadoDisplay   = estadoDisplay,
    notas           = notas,
    totalPagado     = totalPagado,
    saldoPendiente  = saldoPendiente,
)

package com.reservahotel.reservasapplication.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.reservahotel.reservasapplication.domain.model.Reserva

data class ReservaDto(
    val id: Int = 0,
    @SerializedName("cliente_nombre")    val clienteNombre:    String = "",
    @SerializedName("habitacion_numero") val habitacionNumero: String = "",
    @SerializedName("fecha_entrada")     val fechaEntrada:     String = "",
    @SerializedName("fecha_salida")      val fechaSalida:      String = "",
    val noches: Int = 0,
    val estado: String = "",
    @SerializedName("estado_display")    val estadoDisplay:    String = "",
    val total: String = "",
    @SerializedName("created_at")        val createdAt:        String = "",
)

// DTO para crear una reserva nueva
data class ReservaCreateDto(
    val cliente: Int,
    val habitacion: Int,
    val servicios: List<Int> = emptyList(),
    @SerializedName("fecha_entrada") val fechaEntrada: String,
    @SerializedName("fecha_salida")  val fechaSalida:  String,
    val observaciones: String = "",
)

fun ReservaDto.toDomain() = Reserva(
    id               = id,
    clienteNombre    = clienteNombre,
    habitacionNumero = habitacionNumero,
    fechaEntrada     = fechaEntrada,
    fechaSalida      = fechaSalida,
    noches           = noches,
    estado           = estado,
    estadoDisplay    = estadoDisplay,
    total            = total,
    createdAt        = createdAt,
)

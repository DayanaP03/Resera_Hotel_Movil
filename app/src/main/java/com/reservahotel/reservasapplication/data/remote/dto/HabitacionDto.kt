package com.reservahotel.reservasapplication.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.reservahotel.reservasapplication.domain.model.Habitacion

data class HabitacionDto(
    val id: Int = 0,
    val numero: String = "",
    val tipo: String = "",
    @SerializedName("tipo_display")    val tipoDisplay:    String  = "",
    @SerializedName("precio_noche")    val precioNoche:    String  = "0.00",
    val estado: String = "",
    @SerializedName("estado_display")  val estadoDisplay:  String  = "",
    val descripcion: String? = null,
    val capacidad: Int = 1,
)

fun HabitacionDto.toDomain() = Habitacion(
    id           = id,
    numero       = numero,
    tipo         = tipo,
    tipoDisplay  = tipoDisplay,
    precio_noche = precioNoche,
    estado       = estado,
    estadoDisplay = estadoDisplay,
    descripcion  = descripcion,
    capacidad    = capacidad,
)

fun Habitacion.toDto() = HabitacionDto(
    id          = id,
    numero      = numero,
    tipo        = tipo,
    precioNoche = precio_noche,
    estado      = estado,
    descripcion = descripcion,
    capacidad   = capacidad,
)

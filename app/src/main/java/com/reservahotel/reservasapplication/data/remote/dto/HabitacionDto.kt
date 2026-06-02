package com.reservahotel.reservasapplication.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.reservahotel.reservasapplication.domain.model.Habitacion

data class HabitacionDto(
    val id: Int,
    val numero: String,
    val tipo: String,
    @SerializedName("precio_noche") val precioNoche: String,
    val estado: String,
    val descripcion: String?,
    val capacidad: Int
)

fun HabitacionDto.toDomain() = Habitacion(
    id = id,
    numero = numero,
    tipo = tipo,
    precio_noche = precioNoche,
    estado = estado,
    descripcion = descripcion,
    capacidad = capacidad
)

fun Habitacion.toDto() = HabitacionDto(
    id = id,
    numero = numero,
    tipo = tipo,
    precioNoche = precio_noche,
    estado = estado,
    descripcion = descripcion,
    capacidad = capacidad
)

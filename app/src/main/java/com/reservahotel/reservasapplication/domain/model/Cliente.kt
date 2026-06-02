package com.reservahotel.reservasapplication.domain.model

data class Cliente(
    val id: Int,
    val nombre: String,
    val telefono: String?,
    val direccion: String?,
    val correo: String
)

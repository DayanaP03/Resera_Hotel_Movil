package com.reservahotel.reservasapplication.domain.model

data class Usuario(
    val id: Int,
    val username: String,
    val email: String,
    val rol: String,
    val isActive: Boolean = true,
    val isStaff: Boolean = false,
    val createdAt: String = "",
)

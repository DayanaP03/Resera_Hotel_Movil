package com.reservahotel.reservasapplication.domain.repository

import com.reservahotel.reservasapplication.domain.model.Reserva

interface ReservaRepository {
    suspend fun getReservas(): Result<List<Reserva>>
    suspend fun createReserva(clienteId: Int, habitacionId: Int, fechaEntrada: String, fechaSalida: String): Result<Unit>
    suspend fun cancelarReserva(id: Int): Result<String>
    suspend fun deleteReserva(id: Int): Result<Unit>
}

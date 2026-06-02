package com.reservahotel.reservasapplication.domain.repository

import com.reservahotel.reservasapplication.domain.model.Habitacion

interface HabitacionRepository {
    suspend fun getHabitaciones(): Result<List<Habitacion>>
    suspend fun getHabitacion(id: Int): Result<Habitacion>
    suspend fun createHabitacion(habitacion: Habitacion): Result<Habitacion>
    suspend fun updateHabitacion(id: Int, habitacion: Habitacion): Result<Habitacion>
    suspend fun deleteHabitacion(id: Int): Result<Unit>
}

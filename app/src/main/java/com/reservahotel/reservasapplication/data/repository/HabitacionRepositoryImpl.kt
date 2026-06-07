package com.reservahotel.reservasapplication.data.repository

import com.reservahotel.reservasapplication.data.remote.api.HabitacionApi
import com.reservahotel.reservasapplication.data.remote.dto.toDomain
import com.reservahotel.reservasapplication.data.remote.dto.toDto
import com.reservahotel.reservasapplication.domain.model.Habitacion
import com.reservahotel.reservasapplication.domain.repository.HabitacionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitacionRepositoryImpl @Inject constructor(
    private val api: HabitacionApi,
) : HabitacionRepository {

    override suspend fun getHabitaciones(): Result<List<Habitacion>> = runCatching {
        val response = api.getHabitaciones()
        if (response.isSuccessful) {
            response.body()?.results?.map { it.toDomain() } ?: emptyList()
        } else {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun getHabitacion(id: Int): Result<Habitacion> = runCatching {
        val response = api.getHabitacion(id)
        if (response.isSuccessful) {
            response.body()?.toDomain() ?: throw Exception("Habitación no encontrada")
        } else {
            error("Error ${response.code()}")
        }
    }

    override suspend fun createHabitacion(habitacion: Habitacion): Result<Habitacion> = runCatching {
        val response = api.createHabitacion(habitacion.toDto())
        if (response.isSuccessful) {
            response.body()?.toDomain() ?: throw Exception("Error al crear habitación")
        } else {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun updateHabitacion(id: Int, habitacion: Habitacion): Result<Habitacion> = runCatching {
        val response = api.updateHabitacion(id, habitacion.toDto())
        if (response.isSuccessful) {
            response.body()?.toDomain() ?: throw Exception("Error al actualizar habitación")
        } else {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun deleteHabitacion(id: Int): Result<Unit> = runCatching {
        val response = api.deleteHabitacion(id)
        if (!response.isSuccessful) error("Error ${response.code()}: ${response.errorBody()?.string()}")
    }
}

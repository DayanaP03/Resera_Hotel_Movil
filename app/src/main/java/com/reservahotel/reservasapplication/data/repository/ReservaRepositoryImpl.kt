package com.reservahotel.reservasapplication.data.repository

import com.reservahotel.reservasapplication.data.remote.api.ReservaApi
import com.reservahotel.reservasapplication.data.remote.dto.ReservaCreateDto
import com.reservahotel.reservasapplication.data.remote.dto.toDomain
import com.reservahotel.reservasapplication.domain.model.Reserva
import com.reservahotel.reservasapplication.domain.repository.ReservaRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReservaRepositoryImpl @Inject constructor(
    private val api: ReservaApi,
) : ReservaRepository {

    override suspend fun getReservas(): Result<List<Reserva>> = runCatching {
        val r = api.getReservas()
        if (r.isSuccessful) r.body()?.results?.map { it.toDomain() } ?: emptyList()
        else error("Error ${r.code()}: ${r.errorBody()?.string()}")
    }

    override suspend fun createReserva(
        clienteId: Int,
        habitacionId: Int,
        fechaEntrada: String,
        fechaSalida: String,
    ): Result<Unit> = runCatching {
        val r = api.createReserva(
            ReservaCreateDto(
                cliente      = clienteId,
                habitacion   = habitacionId,
                fechaEntrada = fechaEntrada,
                fechaSalida  = fechaSalida,
            )
        )
        if (!r.isSuccessful) error("Error ${r.code()}: ${r.errorBody()?.string()}")
    }

    override suspend fun cancelarReserva(id: Int): Result<String> = runCatching {
        val r = api.cancelarReserva(id)
        if (r.isSuccessful) r.body()?.get("mensaje") ?: "Reserva cancelada"
        else error("Error ${r.code()}: ${r.errorBody()?.string()}")
    }

    override suspend fun deleteReserva(id: Int): Result<Unit> = runCatching {
        val r = api.deleteReserva(id)
        if (!r.isSuccessful) error("Error ${r.code()}: ${r.errorBody()?.string()}")
    }
}

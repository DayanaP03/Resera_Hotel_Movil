package com.reservahotel.reservasapplication.data.repository

import com.reservahotel.reservasapplication.data.remote.api.ServicioApi
import com.reservahotel.reservasapplication.data.remote.dto.toDomain
import com.reservahotel.reservasapplication.data.remote.dto.toDto
import com.reservahotel.reservasapplication.domain.model.Servicio
import com.reservahotel.reservasapplication.domain.repository.ServicioRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServicioRepositoryImpl @Inject constructor(
    private val api: ServicioApi,
) : ServicioRepository {

    override suspend fun getServicios(): Result<List<Servicio>> = runCatching {
        val r = api.getServicios()
        if (r.isSuccessful) r.body()?.results?.map { it.toDomain() } ?: emptyList()
        else error("Error ${r.code()}: ${r.errorBody()?.string()}")
    }

    override suspend fun createServicio(servicio: Servicio): Result<Servicio> = runCatching {
        val r = api.createServicio(servicio.toDto())
        if (r.isSuccessful) r.body()?.toDomain() ?: error("Respuesta vacía")
        else error("Error ${r.code()}: ${r.errorBody()?.string()}")
    }

    override suspend fun toggleActivo(id: Int, activo: Boolean): Result<Servicio> = runCatching {
        val r = api.updateServicio(id, mapOf("activo" to activo))
        if (r.isSuccessful) r.body()?.toDomain() ?: error("Respuesta vacía")
        else error("Error ${r.code()}: ${r.errorBody()?.string()}")
    }

    override suspend fun deleteServicio(id: Int): Result<Unit> = runCatching {
        val r = api.deleteServicio(id)
        if (!r.isSuccessful) error("Error ${r.code()}: ${r.errorBody()?.string()}")
    }
}

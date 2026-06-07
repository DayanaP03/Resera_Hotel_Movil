package com.reservahotel.reservasapplication.domain.repository

import com.reservahotel.reservasapplication.domain.model.Servicio

interface ServicioRepository {
    suspend fun getServicios(): Result<List<Servicio>>
    suspend fun createServicio(servicio: Servicio): Result<Servicio>
    suspend fun toggleActivo(id: Int, activo: Boolean): Result<Servicio>
    suspend fun deleteServicio(id: Int): Result<Unit>
}

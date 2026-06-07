package com.reservahotel.reservasapplication.data.repository

import com.reservahotel.reservasapplication.data.remote.api.FacturaApi
import com.reservahotel.reservasapplication.data.remote.dto.toDomain
import com.reservahotel.reservasapplication.domain.model.Factura
import com.reservahotel.reservasapplication.domain.repository.FacturaRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FacturaRepositoryImpl @Inject constructor(
    private val api: FacturaApi,
) : FacturaRepository {

    override suspend fun getFacturas(): Result<List<Factura>> = runCatching {
        val r = api.getFacturas()
        if (r.isSuccessful) r.body()?.results?.map { it.toDomain() } ?: emptyList()
        else error("Error ${r.code()}: ${r.errorBody()?.string()}")
    }
}

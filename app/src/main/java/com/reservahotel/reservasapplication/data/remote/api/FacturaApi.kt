package com.reservahotel.reservasapplication.data.remote.api

import com.reservahotel.reservasapplication.data.remote.dto.FacturaDto
import com.reservahotel.reservasapplication.data.remote.dto.PaginatedDto
import retrofit2.Response
import retrofit2.http.*

interface FacturaApi {

    @GET("facturas/")
    suspend fun getFacturas(): Response<PaginatedDto<FacturaDto>>

    @GET("facturas/{id}/")
    suspend fun getFactura(@Path("id") id: Int): Response<FacturaDto>
}

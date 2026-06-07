package com.reservahotel.reservasapplication.data.remote.api

import com.reservahotel.reservasapplication.data.remote.dto.PaginatedDto
import com.reservahotel.reservasapplication.data.remote.dto.ServicioDto
import retrofit2.Response
import retrofit2.http.*

interface ServicioApi {

    @GET("servicios/")
    suspend fun getServicios(): Response<PaginatedDto<ServicioDto>>

    @GET("servicios/{id}/")
    suspend fun getServicio(@Path("id") id: Int): Response<ServicioDto>

    @POST("servicios/")
    suspend fun createServicio(@Body body: ServicioDto): Response<ServicioDto>

    @PATCH("servicios/{id}/")
    suspend fun updateServicio(
        @Path("id") id: Int,
        @Body body: Map<String, @JvmSuppressWildcards Any>,
    ): Response<ServicioDto>

    @DELETE("servicios/{id}/")
    suspend fun deleteServicio(@Path("id") id: Int): Response<Unit>
}

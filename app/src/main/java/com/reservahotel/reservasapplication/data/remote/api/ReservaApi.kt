package com.reservahotel.reservasapplication.data.remote.api

import com.reservahotel.reservasapplication.data.remote.dto.PaginatedDto
import com.reservahotel.reservasapplication.data.remote.dto.ReservaDto
import com.reservahotel.reservasapplication.data.remote.dto.ReservaCreateDto
import retrofit2.Response
import retrofit2.http.*

interface ReservaApi {

    @GET("reservas/")
    suspend fun getReservas(): Response<PaginatedDto<ReservaDto>>

    @POST("reservas/")
    suspend fun createReserva(@Body body: ReservaCreateDto): Response<ReservaCreateDto>

    @POST("reservas/{id}/cancelar/")
    suspend fun cancelarReserva(@Path("id") id: Int): Response<Map<String, String>>

    @DELETE("reservas/{id}/")
    suspend fun deleteReserva(@Path("id") id: Int): Response<Unit>
}

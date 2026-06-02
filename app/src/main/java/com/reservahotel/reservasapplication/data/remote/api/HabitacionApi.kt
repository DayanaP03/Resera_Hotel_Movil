package com.reservahotel.reservasapplication.data.remote.api

import com.reservahotel.reservasapplication.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface HabitacionApi {
    @GET("habitaciones/")
    suspend fun getHabitaciones(): Response<PaginatedDto<HabitacionDto>>

    @GET("habitaciones/{id}/")
    suspend fun getHabitacion(@Path("id") id: Int): Response<HabitacionDto>

    @POST("habitaciones/")
    suspend fun createHabitacion(@Body habitacion: HabitacionDto): Response<HabitacionDto>

    @PUT("habitaciones/{id}/")
    suspend fun updateHabitacion(@Path("id") id: Int, @Body habitacion: HabitacionDto): Response<HabitacionDto>

    @DELETE("habitaciones/{id}/")
    suspend fun deleteHabitacion(@Path("id") id: Int): Response<Unit>
}

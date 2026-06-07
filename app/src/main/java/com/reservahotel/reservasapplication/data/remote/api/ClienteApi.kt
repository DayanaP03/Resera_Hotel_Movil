package com.reservahotel.reservasapplication.data.remote.api

import com.reservahotel.reservasapplication.data.remote.dto.ClienteDto
import com.reservahotel.reservasapplication.data.remote.dto.ClienteRequestDto
import com.reservahotel.reservasapplication.data.remote.dto.PaginatedDto
import retrofit2.Response
import retrofit2.http.*

interface ClienteApi {

    @GET("clientes/")
    suspend fun getClientes(): Response<PaginatedDto<ClienteDto>>

    @GET("clientes/{id}/")
    suspend fun getCliente(@Path("id") id: Int): Response<ClienteDto>

    @POST("clientes/")
    suspend fun createCliente(@Body body: ClienteRequestDto): Response<ClienteDto>

    @PATCH("clientes/{id}/")
    suspend fun updateCliente(
        @Path("id") id: Int,
        @Body body: ClienteRequestDto,
    ): Response<ClienteDto>

    @DELETE("clientes/{id}/")
    suspend fun deleteCliente(@Path("id") id: Int): Response<Unit>
}

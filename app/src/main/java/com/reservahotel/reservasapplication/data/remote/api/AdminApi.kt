package com.reservahotel.reservasapplication.data.remote.api

import com.reservahotel.reservasapplication.data.remote.dto.UsuarioDto
import retrofit2.Response
import retrofit2.http.*

interface AdminApi {

    @GET("admin/users/")
    suspend fun listUsers(): Response<List<UsuarioDto>>

    @PATCH("admin/users/{id}/")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body body: Map<String, @JvmSuppressWildcards Any>,
    ): Response<UsuarioDto>
}

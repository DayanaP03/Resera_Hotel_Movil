package com.reservahotel.reservasapplication.data.repository

import com.reservahotel.reservasapplication.data.remote.api.ClienteApi
import com.reservahotel.reservasapplication.data.remote.dto.toDomain
import com.reservahotel.reservasapplication.data.remote.dto.toRequestDto
import com.reservahotel.reservasapplication.domain.model.Cliente
import com.reservahotel.reservasapplication.domain.repository.ClienteRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClienteRepositoryImpl @Inject constructor(
    private val api: ClienteApi,
) : ClienteRepository {

    override suspend fun getClientes(): Result<List<Cliente>> = runCatching {
        val response = api.getClientes()
        if (response.isSuccessful) {
            response.body()?.results?.map { it.toDomain() } ?: emptyList()
        } else {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun getCliente(id: Int): Result<Cliente> = runCatching {
        val response = api.getCliente(id)
        if (response.isSuccessful) {
            response.body()?.toDomain() ?: throw Exception("Cliente no encontrado")
        } else {
            error("Error ${response.code()}")
        }
    }

    override suspend fun createCliente(cliente: Cliente): Result<Cliente> = runCatching {
        val response = api.createCliente(cliente.toRequestDto())
        if (response.isSuccessful) {
            response.body()?.toDomain() ?: throw Exception("Error al crear cliente")
        } else {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun updateCliente(id: Int, cliente: Cliente): Result<Cliente> = runCatching {
        val response = api.updateCliente(id, cliente.toRequestDto())
        if (response.isSuccessful) {
            response.body()?.toDomain() ?: throw Exception("Error al actualizar cliente")
        } else {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun deleteCliente(id: Int): Result<Unit> = runCatching {
        val response = api.deleteCliente(id)
        if (!response.isSuccessful) error("Error ${response.code()}: ${response.errorBody()?.string()}")
    }
}

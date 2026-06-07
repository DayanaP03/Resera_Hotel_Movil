package com.reservahotel.reservasapplication.domain.repository

import com.reservahotel.reservasapplication.domain.model.Cliente

interface ClienteRepository {
    suspend fun getClientes(): Result<List<Cliente>>
    suspend fun getCliente(id: Int): Result<Cliente>
    suspend fun createCliente(cliente: Cliente): Result<Cliente>
    suspend fun updateCliente(id: Int, cliente: Cliente): Result<Cliente>
    suspend fun deleteCliente(id: Int): Result<Unit>
}

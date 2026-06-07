package com.reservahotel.reservasapplication.domain.repository

import com.reservahotel.reservasapplication.domain.model.Factura

interface FacturaRepository {
    suspend fun getFacturas(): Result<List<Factura>>
}

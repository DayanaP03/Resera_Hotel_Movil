package com.reservahotel.reservasapplication.presentation.client

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reservahotel.reservasapplication.domain.model.Factura
import com.reservahotel.reservasapplication.domain.model.Reserva
import com.reservahotel.reservasapplication.domain.repository.FacturaRepository
import com.reservahotel.reservasapplication.domain.repository.ReservaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservaViewModel @Inject constructor(
    private val reservaRepo: ReservaRepository,
    private val facturaRepo: FacturaRepository,
) : ViewModel() {

    var state by mutableStateOf(ReservaState())
        private set

    init {
        getReservas()
        getFacturas()
    }

    fun getReservas() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            reservaRepo.getReservas()
                .onSuccess { state = state.copy(reservas = it, isLoading = false) }
                .onFailure { state = state.copy(isLoading = false, error = it.message) }
        }
    }

    fun getFacturas() {
        viewModelScope.launch {
            facturaRepo.getFacturas()
                .onSuccess { state = state.copy(facturas = it) }
                .onFailure { /* silencioso, no crítico */ }
        }
    }

    fun crearReserva(clienteId: Int, habitacionId: Int, fechaEntrada: String, fechaSalida: String) {
        if (clienteId <= 0) {
            state = state.copy(errorCrear = "Error: ID de Cliente es $clienteId. Debes cerrar sesión y volver a entrar.")
            Log.e("RESERVA_CHECK", "No se puede reservar porque clienteId es $clienteId")
            return
        }
        viewModelScope.launch {
            Log.d("CLIENTE_ID", "Intentando reserva con ID de cliente: $clienteId")
            state = state.copy(isCreating = true, errorCrear = null, successMessage = null)
            reservaRepo.createReserva(clienteId, habitacionId, fechaEntrada, fechaSalida)
                .onSuccess {
                    state = state.copy(
                        isCreating     = false,
                        successMessage = "¡Reserva creada exitosamente!",
                        mostrarFormReserva = false,
                    )
                    getReservas()
                }
                .onFailure {
                    state = state.copy(isCreating = false, errorCrear = it.message)
                }
        }
    }

    fun cancelarReserva(id: Int) {
        viewModelScope.launch {
            reservaRepo.cancelarReserva(id)
                .onSuccess { msg ->
                    state = state.copy(successMessage = msg)
                    getReservas()
                }
                .onFailure { state = state.copy(error = it.message) }
        }
    }

    fun deleteReserva(id: Int) {
        viewModelScope.launch {
            reservaRepo.deleteReserva(id)
                .onSuccess { getReservas() }
                .onFailure { state = state.copy(error = it.message) }
        }
    }

    fun abrirFormReserva(habitacionId: Int) {
        state = state.copy(mostrarFormReserva = true, habitacionSeleccionadaId = habitacionId, errorCrear = null)
    }

    fun cerrarFormReserva() {
        state = state.copy(mostrarFormReserva = false, errorCrear = null)
    }

    fun clearSuccess() {
        state = state.copy(successMessage = null)
    }

    fun clearError() {
        state = state.copy(error = null, errorCrear = null)
    }
}

data class ReservaState(
    val reservas:              List<Reserva>  = emptyList(),
    val facturas:              List<Factura>  = emptyList(),
    val isLoading:             Boolean        = false,
    val isCreating:            Boolean        = false,
    val error:                 String?        = null,
    val errorCrear:            String?        = null,
    val successMessage:        String?        = null,
    val mostrarFormReserva:    Boolean        = false,
    val habitacionSeleccionadaId: Int         = 0,
)

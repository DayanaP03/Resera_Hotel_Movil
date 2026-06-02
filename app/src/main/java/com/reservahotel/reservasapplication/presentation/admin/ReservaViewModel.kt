package com.reservahotel.reservasapplication.presentation.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reservahotel.reservasapplication.domain.model.Reserva
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservaViewModel @Inject constructor() : ViewModel() {

    var state by mutableStateOf(ReservaState())
        private set

    // Lista simulada con datos de prueba adaptados a tu Reserva.kt
    private var reservasSimuladas = mutableListOf(
        Reserva(
            id = 1,
            cliente = 10, // ID ficticio del cliente Juan Pérez
            habitacion = 101, // Habitación 101
            servicios = listOf(1, 2), // WiFi y Desayuno
            fecha_entrada = "2026-06-05",
            fecha_salida = "2026-06-10",
            estado = "Confirmada",
            observaciones = "Cliente solicita cama adicional."
        ),
        Reserva(
            id = 2,
            cliente = 14, // ID ficticio de cliente María López
            habitacion = 102, // Suite Presidencial
            servicios = listOf(3), // Jacuzzi/Spa
            fecha_entrada = "2026-06-12",
            fecha_salida = "2026-06-15",
            estado = "Pendiente",
            observaciones = "Late check-in programado para las 11 PM."
        ),
        Reserva(
            id = 3,
            cliente = 22,
            habitacion = 201,
            servicios = emptyList(),
            fecha_entrada = "2026-05-20",
            fecha_salida = "2026-05-25",
            estado = "Completada",
            observaciones = null
        )
    )

    init {
        getReservas()
    }

    fun getReservas() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            // .toList() para que Compose detecte cambios al agregar o eliminar
            state = state.copy(reservas = reservasSimuladas.toList(), isLoading = false)
        }
    }

    fun deleteReserva(id: Int) {
        viewModelScope.launch {
            reservasSimuladas.removeAll { it.id == id }
            getReservas()
        }
    }

    fun saveReserva(reserva: Reserva, isEdit: Boolean) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)

            if (isEdit) {
                val index = reservasSimuladas.indexOfFirst { it.id == reserva.id }
                if (index != -1) {
                    reservasSimuladas[index] = reserva
                }
            } else {
                val nuevoId = (reservasSimuladas.maxOfOrNull { it.id } ?: 0) + 1
                reservasSimuladas.add(reserva.copy(id = nuevoId))
            }

            getReservas()
            state = state.copy(isLoading = false, isSuccess = true)
        }
    }

    fun resetSuccess() {
        state = state.copy(isSuccess = false)
    }

    fun getReservaById(id: Int): Reserva? {
        return state.reservas.find { it.id == id }
    }
}

data class ReservaState(
    val reservas: List<Reserva> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
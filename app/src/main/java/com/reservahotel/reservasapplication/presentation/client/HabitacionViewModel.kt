package com.reservahotel.reservasapplication.presentation.client

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reservahotel.reservasapplication.domain.model.Habitacion
import com.reservahotel.reservasapplication.domain.repository.HabitacionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitacionViewModel @Inject constructor(
    private val repository: HabitacionRepository,
) : ViewModel() {

    var state by mutableStateOf(HabitacionState())
        private set

    init { getHabitaciones() }

    fun getHabitaciones() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            repository.getHabitaciones()
                .onSuccess { list ->
                    // Combinamos las del servidor con las de prueba para que siempre haya datos
                    val availableFromServer = list.filter { it.estado.lowercase() == "disponible" }
                    val allData = (availableFromServer + getMockHabitaciones()).distinctBy { it.numero }
                    
                    state = state.copy(
                        habitaciones = allData,
                        isLoading = false,
                    )
                }
                .onFailure { e ->
                    // Si falla el internet, mostramos solo las de prueba
                    state = state.copy(
                        habitaciones = getMockHabitaciones(),
                        isLoading = false,
                    )
                }
        }
    }

    private fun getMockHabitaciones() = listOf(
        Habitacion(101, "101", "Suite", "Suite Real", "4500", "Disponible", "Disponible", "Lujosa suite con jacuzzi privado y vista panorámica al mar.", 4),
        Habitacion(102, "102", "Sencilla", "Económica", "850", "Disponible", "Disponible", "Habitación acogedora perfecta para viajeros solitarios.", 1),
        Habitacion(201, "201", "Doble", "Doble Premium", "1800", "Disponible", "Disponible", "Dos camas matrimoniales, ideal para amigos o familias pequeñas.", 3),
        Habitacion(202, "202", "Suite", "Suite Nupcial", "3200", "Disponible", "Disponible", "Decoración romántica y servicios exclusivos para parejas.", 2),
        Habitacion(301, "301", "Deluxe", "Familiar King", "2500", "Disponible", "Disponible", "Espaciosa con cama King y área de juegos para niños.", 5),
        Habitacion(302, "302", "Sencilla", "Confort Business", "1100", "Disponible", "Disponible", "Equipada con escritorio ergonómico y Wi-Fi de alta velocidad.", 1),
        Habitacion(401, "401", "Doble", "Vista al Jardín", "1600", "Disponible", "Disponible", "Salida directa a las áreas verdes y mucha luz natural.", 2),
        Habitacion(402, "402", "Deluxe", "Terraza Privada", "2900", "Disponible", "Disponible", "Incluye una terraza amplia con hamacas y mini-bar.", 4),
        Habitacion(501, "501", "Suite", "Executive Suite", "3800", "Disponible", "Disponible", "Nivel ejecutivo con acceso a la sala VIP del hotel.", 2),
        Habitacion(502, "502", "Sencilla", "Standard", "950", "Disponible", "Disponible", "Funcional y cómoda, con todos los servicios básicos.", 1)
    )

    fun getHabitacionById(id: Int): Habitacion? = state.habitaciones.find { it.id == id }
}

data class HabitacionState(
    val habitaciones: List<Habitacion> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
)

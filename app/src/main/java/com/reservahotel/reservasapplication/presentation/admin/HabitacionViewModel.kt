package com.reservahotel.reservasapplication.presentation.admin

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
    private val repository: HabitacionRepository
) : ViewModel() {

    var state by mutableStateOf(HabitacionState())
        private set

    private var habitacionesSimuladas = mutableListOf(
        Habitacion(
            id = 1,
            numero = "101",
            tipo = "Matrimonial Standard",
            precio_noche = "45.0",
            estado = "Disponible",
            descripcion = "Una hermosa habitación con cama matrimonial y vista interna.",
            capacidad = 2
        ),
        Habitacion(
            id = 2,
            numero = "102",
            tipo = "Suite Presidencial",
            precio_noche = "120.0",
            estado = "Disponible",
            descripcion = "Suite de lujo con jacuzzi y balcón hacia la calle principal.",
            capacidad = 4
        ),
        Habitacion(
            id = 3,
            numero = "201",
            tipo = "Doble Familiar",
            precio_noche = "75.0",
            estado = "Ocupada",
            descripcion = "Dos camas de plaza y media, ideal para viajes familiares.",
            capacidad = 3
        )
    )

    init {
        getHabitaciones()
    }

    fun getHabitaciones() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            // ¡EL TRUCO CLAVE!: Usamos .toList() para crear una copia nueva de la lista.
            // Esto obliga a Jetpack Compose a redibujar la pantalla con la nueva habitación.
            state = state.copy(habitaciones = habitacionesSimuladas.toList(), isLoading = false)
        }
    }

    fun deleteHabitacion(id: Int) {
        viewModelScope.launch {
            habitacionesSimuladas.removeAll { it.id == id }
            getHabitaciones()
        }
    }

    fun saveHabitacion(habitacion: Habitacion, isEdit: Boolean) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)

            if (isEdit) {
                val index = habitacionesSimuladas.indexOfFirst { it.id == habitacion.id }
                if (index != -1) {
                    habitacionesSimuladas[index] = habitacion
                }
            } else {
                val nuevoId = (habitacionesSimuladas.maxOfOrNull { it.id } ?: 0) + 1
                habitacionesSimuladas.add(habitacion.copy(id = nuevoId))
            }

            getHabitaciones()
            // Marcamos éxito para que la pantalla del formulario se cierre sola
            state = state.copy(isLoading = false, isSuccess = true)
        }
    }

    fun resetSuccess() {
        state = state.copy(isSuccess = false)
    }

    fun getHabitacionById(id: Int): Habitacion? {
        return state.habitaciones.find { it.id == id }
    }
}

data class HabitacionState(
    val habitaciones: List<Habitacion> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
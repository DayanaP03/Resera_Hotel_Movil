package com.reservahotel.reservasapplication.presentation.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reservahotel.reservasapplication.domain.model.Servicio
import com.reservahotel.reservasapplication.domain.repository.ServicioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// Renombrado internamente a ServicioViewModel pero mantenemos el nombre CategoryViewModel
// para no romper los composables existentes que lo inyectan con hiltViewModel()
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: ServicioRepository,
) : ViewModel() {

    var state by mutableStateOf(CategoryState())
        private set

    init { loadServicios() }

    fun loadServicios() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            repository.getServicios()
                .onSuccess { list ->
                    state = state.copy(servicios = list, isLoading = false)
                }
                .onFailure { e ->
                    state = state.copy(isLoading = false, error = e.message)
                }
        }
    }

    fun toggleActivo(id: Int) {
        viewModelScope.launch {
            val current = state.servicios.find { it.id == id } ?: return@launch
            repository.toggleActivo(id, !current.activo)
                .onSuccess { updated ->
                    state = state.copy(
                        servicios = state.servicios.map { if (it.id == id) updated else it }
                    )
                }
                .onFailure { e -> state = state.copy(error = e.message) }
        }
    }

    fun addServicio(nombre: String, descripcion: String, precio: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            val nuevo = Servicio(id = 0, nombre = nombre, descripcion = descripcion, precio = precio, activo = true)
            repository.createServicio(nuevo)
                .onSuccess { loadServicios() }
                .onFailure { e -> state = state.copy(isLoading = false, error = e.message) }
        }
    }

    fun deleteServicio(id: Int) {
        viewModelScope.launch {
            repository.deleteServicio(id)
                .onSuccess { loadServicios() }
                .onFailure { e -> state = state.copy(error = e.message) }
        }
    }
}

data class CategoryState(
    val servicios: List<Servicio> = emptyList(),
    val isLoading: Boolean        = false,
    val error:     String?        = null,
)

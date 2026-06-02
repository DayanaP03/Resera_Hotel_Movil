package com.reservahotel.reservasapplication.presentation.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reservahotel.reservasapplication.domain.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor() : ViewModel() {

    var state by mutableStateOf(CategoryState())
        private set

    private var categoriasSimuladas = mutableListOf(
        Category(1, "Económica", "economica", "Habitaciones sencillas con servicios básicos.", true, 12, "2026-01-15"),
        Category(2, "Ejecutiva", "ejecutiva", "Diseñadas para viajes de negocios. Cuenta con escritorio.", true, 8, "2026-02-10"),
        Category(3, "Premium Suites", "premium-suites", "Máximo lujo. Incluye jacuzzi privado.", false, 4, "2026-03-01")
    )

    init { getCategorias() }

    fun getCategorias() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            state = state.copy(categorias = categoriasSimuladas.toList(), isLoading = false)
        }
    }

    fun toggleCategoryStatus(id: Int) {
        viewModelScope.launch {
            val index = categoriasSimuladas.indexOfFirst { it.id == id }
            if (index != -1) {
                categoriasSimuladas[index] = categoriasSimuladas[index].copy(isActive = !categoriasSimuladas[index].isActive)
                getCategorias()
            }
        }
    }

    // =========================================================================
    // NUEVA FUNCIÓN: Guarda dinámicamente una nueva categoría creada en la app
    // =========================================================================
    fun addCategory(name: String, description: String) {
        viewModelScope.launch {
            val nuevoId = (categoriasSimuladas.maxOfOrNull { it.id } ?: 0) + 1
            val nuevaCat = Category(
                id = nuevoId,
                name = name,
                slug = name.lowercase().replace(" ", "-"),
                description = description,
                isActive = true,
                totalProducts = 0,
                createdAt = "2026-06-01"
            )
            categoriasSimuladas.add(nuevaCat)
            getCategorias() // Refresca la pantalla al instante
        }
    }
}

data class CategoryState(
    val categorias: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
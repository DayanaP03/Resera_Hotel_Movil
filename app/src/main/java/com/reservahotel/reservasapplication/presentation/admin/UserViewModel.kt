package com.reservahotel.reservasapplication.presentation.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// Modelo de datos local para el ejercicio
data class LocalUser(
    val id: Int,
    val username: String,
    val email: String,
    val rol: String, // "admin", "staff", "cliente"
    val isActive: Boolean,
    val fechaRegistro: String
)

@HiltViewModel
class UserViewModel @Inject constructor() : ViewModel() {

    var state by mutableStateOf(UserState())
        private set

    private var usuariosSimulados = mutableListOf(
        LocalUser(1, "Carlos Mendoza", "carlos.admin@hotel.com", "admin", true, "2026-01-10"),
        LocalUser(2, "Lucía Fernández", "lucia.staff@hotel.com", "staff", true, "2026-03-14"),
        LocalUser(3, "Alejandro Gómez", "alejandro.g@gmail.com", "cliente", true, "2026-05-20"),
        LocalUser(4, "Sofía Reyes", "sofia.reyes@outlook.com", "cliente", false, "2026-05-28")
    )

    init { getUsuarios() }

    fun getUsuarios() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            state = state.copy(usuarios = usuariosSimulados.toList(), isLoading = false)
        }
    }

    // Cambiar el rol dinámicamente
    fun updateChangeRol(id: Int, nuevoRol: String) {
        viewModelScope.launch {
            val index = usuariosSimulados.indexOfFirst { it.id == id }
            if (index != -1) {
                usuariosSimulados[index] = usuariosSimulados[index].copy(rol = nuevoRol)
                getUsuarios()
            }
        }
    }

    // Activar o desactivar cuenta
    fun toggleUserStatus(id: Int) {
        viewModelScope.launch {
            val index = usuariosSimulados.indexOfFirst { it.id == id }
            if (index != -1) {
                usuariosSimulados[index] = usuariosSimulados[index].copy(isActive = !usuariosSimulados[index].isActive)
                getUsuarios()
            }
        }
    }
}

data class UserState(
    val usuarios: List<LocalUser> = emptyList(),
    val isLoading: Boolean = false
)
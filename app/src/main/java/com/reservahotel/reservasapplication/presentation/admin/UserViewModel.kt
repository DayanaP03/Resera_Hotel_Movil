package com.reservahotel.reservasapplication.presentation.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// 1. Definimos el modelo de datos aquí mismo para evitar errores de referencia
data class LocalUser(
    val id: Int,
    val username: String,
    val email: String,
    val rol: String,
    val isActive: Boolean,
    val fechaRegistro: String
)

// 2. Definimos el estado de la UI
data class UserState(
    val usuarios: List<LocalUser> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class UserViewModel @Inject constructor() : ViewModel() {

    var state by mutableStateOf(UserState())
        private set

    // Lista maestra de datos privados
    private val _usuariosSimulados = mutableListOf(
        LocalUser(1, "Carlos Mendoza", "carlos.admin@hotel.com", "admin", true, "2026-01-10"),
        LocalUser(2, "Lucía Fernández", "lucia.staff@hotel.com", "staff", true, "2026-03-14"),
        LocalUser(3, "Alejandro Gómez", "alejandro.g@gmail.com", "cliente", true, "2026-05-20"),
        LocalUser(4, "Sofía Reyes", "sofia.reyes@outlook.com", "cliente", false, "2026-05-28")
    )

    init {
        refreshState()
    }

    private fun refreshState() {
        state = state.copy(usuarios = _usuariosSimulados.toList())
    }

    fun updateChangeRol(id: Int, nuevoRol: String) {
        val index = _usuariosSimulados.indexOfFirst { it.id == id }
        if (index != -1) {
            _usuariosSimulados[index] = _usuariosSimulados[index].copy(rol = nuevoRol)
            refreshState()
        }
    }

    fun toggleUserStatus(id: Int) {
        val index = _usuariosSimulados.indexOfFirst { it.id == id }
        if (index != -1) {
            val user = _usuariosSimulados[index]
            _usuariosSimulados[index] = user.copy(isActive = !user.isActive)
            refreshState()
        }
    }

    // Dentro de tu clase UserViewModel...
    fun addUser(username: String, email: String, rol: String) {
        val newUser = LocalUser(
            id = (_usuariosSimulados.maxOfOrNull { it.id } ?: 0) + 1,
            username = username,
            email = email,
            rol = rol,
            isActive = true,
            fechaRegistro = "2026-06-02"
        )
        _usuariosSimulados.add(newUser)
        refreshState()
    }
}
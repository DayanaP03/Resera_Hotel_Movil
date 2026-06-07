package com.reservahotel.reservasapplication.presentation.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reservahotel.reservasapplication.domain.model.Usuario
import com.reservahotel.reservasapplication.domain.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// Keep LocalUser as a presentation-layer alias for backward compat with UsersScreen
typealias LocalUser = Usuario

data class UserState(
    val usuarios: List<Usuario> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UsuarioRepository,
) : ViewModel() {

    var state by mutableStateOf(UserState())
        private set

    init { loadUsers() }

    fun loadUsers() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            repository.getUsuarios()
                .onSuccess { usuarios ->
                    state = state.copy(usuarios = usuarios, isLoading = false)
                }
                .onFailure { e ->
                    state = state.copy(isLoading = false, error = e.message)
                }
        }
    }

    fun updateChangeRol(id: Int, nuevoRol: String) {
        viewModelScope.launch {
            val isStaff = nuevoRol == "administrador"
            repository.updateRol(id, nuevoRol, isStaff)
                .onSuccess { updated ->
                    state = state.copy(
                        usuarios = state.usuarios.map { if (it.id == id) updated else it }
                    )
                }
                .onFailure { e -> state = state.copy(error = e.message) }
        }
    }

    fun toggleUserStatus(id: Int) {
        viewModelScope.launch {
            val current = state.usuarios.find { it.id == id } ?: return@launch
            repository.toggleActive(id, !current.isActive)
                .onSuccess { updated ->
                    state = state.copy(
                        usuarios = state.usuarios.map { if (it.id == id) updated else it }
                    )
                }
                .onFailure { e -> state = state.copy(error = e.message) }
        }
    }

    // addUser is not used via API (admin creates users differently),
    // kept as no-op so UsersScreen still compiles
    fun addUser(username: String, email: String, rol: String) {
        // For now reload — a full "create user" endpoint could be added later
        loadUsers()
    }
}

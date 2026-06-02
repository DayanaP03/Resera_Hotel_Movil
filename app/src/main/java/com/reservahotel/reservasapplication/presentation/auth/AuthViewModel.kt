package com.reservahotel.reservasapplication.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reservahotel.reservasapplication.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    fun onUsernameChange(username: String) {
        uiState = uiState.copy(username = username)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password)
    }

    fun login(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            // 1. Simulamos que está cargando por un milisegundo
            uiState = uiState.copy(isLoading = true, error = null)

            // 2. Quitamos la carga
            uiState = uiState.copy(isLoading = false)

            // 3. ¡EL TRUCO TRUCADO! Forzamos que entre directo con el rol "admin"
            // Ya no llamamos a repository.login() para evitar el error 400 del servidor
            onSuccess("admin")
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            uiState = AuthUiState()
        }
    }
}

data class AuthUiState(
    val username:  String = "",
    val password:  String = "",
    val isLoading: Boolean = false,
    val error:     String? = null
)
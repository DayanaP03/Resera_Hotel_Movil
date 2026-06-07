package com.reservahotel.reservasapplication.presentation.auth

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reservahotel.reservasapplication.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AuthVM"

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    fun onUsernameChange(v: String) { uiState = uiState.copy(username = v) }
    fun onPasswordChange(v: String) { uiState = uiState.copy(password = v) }

    fun login(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            repository.login(uiState.username, uiState.password)
                .onSuccess { response ->
                    uiState = uiState.copy(isLoading = false)

                    val userDto = response.resolvedUser()
                    // rol es la fuente de verdad: "administrador" → panel admin
                    val rol = userDto.rol?.trim()?.lowercase() ?: "cliente"

                    Log.d(TAG, "login OK → rol='$rol' → navegando a ${if (rol == "administrador") "ADMIN" else "CLIENTE"}")

                    onSuccess(rol)
                }
                .onFailure { error ->
                    Log.e(TAG, "login FAIL → ${error.message}")
                    uiState = uiState.copy(
                        isLoading = false,
                        error     = error.message ?: "Error al iniciar sesión",
                    )
                }
        }
    }

    fun logout() {
        viewModelScope.launch { repository.logout() }
        uiState = AuthUiState()
    }
}

data class AuthUiState(
    val username:  String  = "",
    val password:  String  = "",
    val isLoading: Boolean = false,
    val error:     String? = null,
)

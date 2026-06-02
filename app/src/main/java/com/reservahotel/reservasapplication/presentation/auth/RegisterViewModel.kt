package com.reservahotel.reservasapplication.presentation.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reservahotel.reservasapplication.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: CategoryRepository
) : ViewModel() {

    // Aquí guardamos lo que el usuario va escribiendo en la pantalla
    var username = mutableStateOf("")
    var password = mutableStateOf("")

    // Estos estados nos dicen si el registro fue exitoso o si hubo un error
    var isRegistered = mutableStateOf(false)
    var errorMessage = mutableStateOf("")

    // Esta función se ejecuta cuando el usuario le da al botón "REGISTRARSE"
    fun onRegisterClick() {
        viewModelScope.launch {
            // Llamamos al repositorio para intentar registrar al usuario
            val result = repository.registerUser(username.value, password.value)

            if (result.isSuccess) {
                isRegistered.value = true
                errorMessage.value = "" // Limpiamos errores si todo sale bien
            } else {
                errorMessage.value = result.exceptionOrNull()?.message ?: "Error al registrar el usuario"
            }
        }
    }
}
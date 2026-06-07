package com.reservahotel.reservasapplication.presentation.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reservahotel.reservasapplication.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {

    var username      = mutableStateOf("")
    var email         = mutableStateOf("")
    var password      = mutableStateOf("")
    var password2     = mutableStateOf("")
    var isRegistered  = mutableStateOf(false)
    var errorMessage  = mutableStateOf("")
    var isLoading     = mutableStateOf(false)

    fun onRegisterClick() {
        if (password.value != password2.value) {
            errorMessage.value = "Las contraseñas no coinciden"
            return
        }
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = ""
            val result = repository.register(
                username  = username.value,
                email     = email.value,
                password  = password.value,
                password2 = password2.value,
            )
            isLoading.value = false
            if (result.isSuccess) {
                isRegistered.value = true
            } else {
                errorMessage.value = result.exceptionOrNull()?.message ?: "Error al registrar"
            }
        }
    }
}

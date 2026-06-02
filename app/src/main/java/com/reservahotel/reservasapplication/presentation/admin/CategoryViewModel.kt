package com.reservahotel.reservasapplication.presentation.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reservahotel.reservasapplication.domain.model.Category
import com.reservahotel.reservasapplication.domain.model.CategoryPayload
import com.reservahotel.reservasapplication.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: CategoryRepository
) : ViewModel() {

    var state by mutableStateOf(CategoryState())
        private set

    init {
        getCategories()
    }

    fun getCategories() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            repository.getCategories()
                .onSuccess {
                    state = state.copy(categories = it, isLoading = false)
                }
                .onFailure {
                    state = state.copy(error = it.message, isLoading = false)
                }
        }
    }

    fun saveCategory(payload: CategoryPayload, id: Int? = null) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            val result = if (id != null) {
                repository.updateCategory(id, payload)
            } else {
                repository.createCategory(payload)
            }

            result.onSuccess {
                getCategories()
                state = state.copy(isLoading = false, isSuccess = true)
            }.onFailure {
                state = state.copy(error = it.message, isLoading = false)
            }
        }
    }

    fun deleteCategory(id: Int) {
        viewModelScope.launch {
            repository.deleteCategory(id)
                .onSuccess {
                    getCategories()
                }
                .onFailure {
                    state = state.copy(error = it.message)
                }
        }
    }

    fun resetSuccess() {
        state = state.copy(isSuccess = false)
    }

    fun getCategoryById(id: Int): Category? {
        return state.categories.find { it.id == id }
    }
}

data class CategoryState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

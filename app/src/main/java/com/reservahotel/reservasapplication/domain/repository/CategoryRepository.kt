package com.reservahotel.reservasapplication.domain.repository

import com.reservahotel.reservasapplication.domain.model.Category
import com.reservahotel.reservasapplication.domain.model.CategoryPayload

interface CategoryRepository {
    suspend fun getCategories(): Result<List<Category>>
    suspend fun getCategory(id: Int): Result<Category>
    suspend fun createCategory(payload: CategoryPayload): Result<Category>
    suspend fun updateCategory(id: Int, payload: CategoryPayload): Result<Category>
    suspend fun deleteCategory(id: Int): Result<Unit>

    // Agrega esto dentro de tu interfaz CategoryRepository
    suspend fun registerUser(username: String, password: String): Result<Unit>

    suspend fun loginUser(username: String, password: String): Result<Unit>
}

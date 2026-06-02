package com.reservahotel.reservasapplication.data.repository

import com.reservahotel.reservasapplication.domain.model.Category
import com.reservahotel.reservasapplication.domain.model.CategoryPayload
import com.reservahotel.reservasapplication.domain.repository.CategoryRepository
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor() : CategoryRepository {

    // 1. SIMULACIÓN DE LOGIN (Para que no te dé más el error 400)
    override suspend fun loginUser(username: String, password: String): Result<Unit> {
        return Result.success(Unit) // Devuelve éxito directo para pruebas
    }

    // 2. SIMULACIÓN DE REGISTRO
    override suspend fun registerUser(username: String, password: String): Result<Unit> {
        return Result.success(Unit) // Devuelve éxito directo para pruebas
    }

    // 3. MÉTODOS DE LAS CATEGORÍAS (Solo una vez cada uno)
    override suspend fun getCategories(): Result<List<Category>> {
        return Result.success(emptyList())
    }

    override suspend fun getCategory(id: Int): Result<Category> {
        TODO("Not yet implemented")
    }

    override suspend fun createCategory(payload: CategoryPayload): Result<Category> {
        TODO("Not yet implemented")
    }

    override suspend fun updateCategory(id: Int, payload: CategoryPayload): Result<Category> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCategory(id: Int): Result<Unit> {
        TODO("Not yet implemented")
    }
}
package com.reservahotel.reservasapplication.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.reservahotel.reservasapplication.domain.model.Category
import com.reservahotel.reservasapplication.domain.model.CategoryPayload

data class CategoryDto(
    val id:          Int,
    val name:        String,
    val slug:        String,
    val description: String,
    @SerializedName("is_active")      val isActive:      Boolean,
    @SerializedName("total_products") val totalProducts: Int,
    @SerializedName("created_at")     val createdAt:     String,
)

data class CategoryRequestDto(
    val name:        String,
    val slug:        String,
    val description: String,
    @SerializedName("is_active") val isActive: Boolean,
)

// ── Mappers ───────────────────────────────────────────────────

fun CategoryDto.toDomain() = Category(
    id            = id,
    name          = name,
    slug          = slug,
    description   = description,
    isActive      = isActive,
    totalProducts = totalProducts,
    createdAt     = createdAt,
)

fun CategoryPayload.toRequest() = CategoryRequestDto(
    name        = name,
    slug        = slug,
    description = description,
    isActive    = isActive,
)

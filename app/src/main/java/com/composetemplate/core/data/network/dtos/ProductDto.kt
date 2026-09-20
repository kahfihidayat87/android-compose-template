package com.composetemplate.core.data.network.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: String,
    val name: String,
    val category: String,
    val price: Int,
    val weight: Int,
    val image: String? = null,
    val emoji: String? = null,
    val isNew: Boolean = false,
    val isBestSeller: Boolean = false,
    val rating: Double = 0.0,
    val reviews: Int = 0,
    val description: String = "",
)

@Serializable
data class ProductsResponse(
    val success: Boolean,
    val products: List<ProductDto>
)

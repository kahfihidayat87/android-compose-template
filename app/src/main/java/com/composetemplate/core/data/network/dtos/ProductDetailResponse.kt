package com.composetemplate.core.data.network.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ProductDetailResponse(
    val success: Boolean,
    val product: ProductDto?
)

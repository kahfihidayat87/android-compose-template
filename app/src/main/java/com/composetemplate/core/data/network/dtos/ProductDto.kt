package com.composetemplate.core.data.network.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: Int,
    val slug: String = "",
    val name: String,
    val category: String,
    val price: Int,
    val oldPrice: Int? = null,
    val badge: String? = null,
    val img: String = "",
    val gallery: List<String> = emptyList(),
    val sizes: List<Int> = emptyList(),
    val stockBySize: Map<String, Int> = emptyMap(),
    val weight: Int = 500,
    val desc: String = "",
)

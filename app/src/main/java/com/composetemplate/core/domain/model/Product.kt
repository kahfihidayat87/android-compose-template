package com.composetemplate.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Int,
    val weight: Int,
    val emoji: String?,
    val isNew: Boolean,
    val isBestSeller: Boolean,
    val rating: Double,
    val reviews: Int,
    val description: String,
)

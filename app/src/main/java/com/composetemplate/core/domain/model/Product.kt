package com.composetemplate.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int,
    val slug: String,
    val name: String,
    val category: String,
    val price: Int,
    val oldPrice: Int?,
    val badge: String?,
    val img: String,
    val sizes: List<Int>,
    val weight: Int,
    val desc: String,
)

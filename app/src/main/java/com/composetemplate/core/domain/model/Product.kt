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
    val stockBySize: Map<String, Int>,
    val weight: Int,
    val desc: String,
) {
    // Helper: stok ukuran tertentu (0 kalau tidak ada data)
    fun stockOf(size: Int): Int = stockBySize[size.toString()] ?: 0

    // Helper: apakah semua ukuran habis?
    val isAllSizesOutOfStock: Boolean
        get() = sizes.isNotEmpty() && sizes.all { stockOf(it) <= 0 }

    // Helper: cek apakah ukuran punya stok
    fun hasStock(size: Int): Boolean = stockOf(size) > 0
}

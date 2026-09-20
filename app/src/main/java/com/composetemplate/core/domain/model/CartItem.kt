package com.composetemplate.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val productId: Int,
    val name: String,
    val price: Int,
    val img: String,
    val size: Int,
    val quantity: Int,
) {
    val subtotal: Int get() = price * quantity

    // Signature untuk cek duplikat (produk sama + ukuran sama = 1 baris)
    val key: String get() = "$productId-$size"
}

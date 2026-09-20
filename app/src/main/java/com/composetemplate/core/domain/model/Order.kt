package com.composetemplate.core.domain.model

data class Order(
    val id: Int,
    val orderNumber: String,
    val subtotal: Int,
    val shippingCost: Int,
    val discountAmount: Int,
    val total: Int,
    val status: String,
    val shippingAddress: String,
    val shippingPhone: String,
    val createdAt: String,
    val resi: String?,
) {
    val statusLabel: String
        get() = when (status) {
            "belum_bayar" -> "Belum Bayar"
            "menunggu_verifikasi" -> "Menunggu Verifikasi"
            "lunas" -> "Lunas"
            "dikirim" -> "Dikirim"
            "selesai" -> "Selesai"
            "dibatalkan" -> "Dibatalkan"
            else -> status
        }
}

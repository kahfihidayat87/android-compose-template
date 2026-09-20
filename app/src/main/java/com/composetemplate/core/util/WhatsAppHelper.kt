package com.composetemplate.core.util

import android.content.Context
import android.content.Intent
import android.net.Uri

object WhatsAppHelper {

    private const val ADMIN_PHONE = "628112645587"

    fun chatAdmin(context: Context, message: String = "") {
        val text = if (message.isBlank()) {
            "Halo A-DHL, saya mau tanya tentang produk sepatu."
        } else {
            message
        }

        val encoded = Uri.encode(text)
        val url = "https://wa.me/$ADMIN_PHONE?text=$encoded"

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(browserIntent)
        }
    }

    fun shareProduct(context: Context, productName: String, price: String, productId: Int) {
        val text = "Cek sepatu A-DHL ini:\n$productName\nHarga: $price\n\nhttps://sepatumu.id/produk/$productId"

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Bagikan via"))
    }
}

package com.composetemplate.core.util

import org.json.JSONObject
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorExtractor {

    fun extract(e: Throwable): String {
        return when (e) {
            is HttpException -> extractFromHttp(e)
            is UnknownHostException -> "Koneksi internet bermasalah. Cek WiFi/data kamu."
            is SocketTimeoutException -> "Server tidak merespons. Coba lagi."
            else -> e.message ?: e.javaClass.simpleName
        }
    }

    private fun extractFromHttp(e: HttpException): String {
        val code = e.code()
        val body = try {
            e.response()?.errorBody()?.string()
        } catch (_: Exception) {
            null
        }

        if (body.isNullOrBlank()) {
            return when (code) {
                401 -> "Sesi login berakhir. Silakan login ulang."
                403 -> "Akses ditolak. Cek email verifikasi."
                404 -> "Data tidak ditemukan."
                500 -> "Server bermasalah. Coba lagi nanti."
                else -> "HTTP error $code"
            }
        }

        return try {
            val json = JSONObject(body)
            // Coba field umum
            json.optString("error").takeIf { it.isNotBlank() }
                ?: json.optString("message").takeIf { it.isNotBlank() }
                ?: json.optJSONArray("errors")?.let { arr ->
                    (0 until arr.length()).joinToString("\n") { arr.optString(it) }
                }
                ?: "HTTP $code: ${body.take(200)}"
        } catch (_: Exception) {
            "HTTP $code: ${body.take(200)}"
        }
    }
}

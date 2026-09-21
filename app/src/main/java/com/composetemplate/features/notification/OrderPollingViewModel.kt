package com.composetemplate.features.notification

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composetemplate.core.data.repositories.OrderRepository
import com.composetemplate.core.data.storage.OrderTrackingStore
import com.composetemplate.core.data.storage.TokenManager
import com.composetemplate.core.util.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderPollingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val orderRepository: OrderRepository,
    private val tokenManager: TokenManager,
    private val trackingStore: OrderTrackingStore,
) : ViewModel() {

    private val STATUS_LABELS = mapOf(
        "belum_bayar" to "Belum Bayar",
        "menunggu_verifikasi" to "Menunggu Verifikasi",
        "lunas" to "Lunas",
        "dikirim" to "Dikirim",
        "selesai" to "Selesai",
        "dibatalkan" to "Dibatalkan",
    )

    fun startPolling() {
        viewModelScope.launch {
            while (isActive) {
                try {
                    pollOnce()
                } catch (e: Exception) {
                    // Diam — coba lagi di polling berikutnya
                }
                delay(60_000L)
            }
        }
    }

    private suspend fun pollOnce() {
        val token = tokenManager.getToken()
        if (token.isNullOrEmpty()) return

        val orders = orderRepository.getMyOrders()
        if (orders.isEmpty()) return

        val lastMap = trackingStore.getStatusMap()
        val newMap = mutableMapOf<String, String>()

        for (order in orders) {
            newMap[order.orderNumber] = order.status
            val previousStatus = lastMap[order.orderNumber]
            if (previousStatus != null && previousStatus != order.status) {
                val label = STATUS_LABELS[order.status] ?: order.status
                NotificationHelper.show(
                    context = context,
                    title = "Pesanan ${order.orderNumber}",
                    message = "Status: $label"
                )
            }
        }

        trackingStore.saveStatusMap(newMap)
    }
}

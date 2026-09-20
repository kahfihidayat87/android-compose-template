package com.composetemplate.core.data.network.dtos

import kotlinx.serialization.Serializable

@Serializable
data class OrderItemRequest(
    val productId: Int,
    val qty: Int,
    val size: Int,
)

@Serializable
data class CourierRequest(
    val courierCode: String,
    val serviceCode: String,
)

@Serializable
data class OrderCreateRequest(
    val items: List<OrderItemRequest>,
    val shippingAddress: String,
    val shippingPhone: String,
    val shippingPostalCode: String,
    val courier: CourierRequest? = null,
    val note: String? = null,
    val guestName: String? = null,
    val guestEmail: String? = null,
)

@Serializable
data class OrderItemDto(
    val productId: Int = 0,
    val name: String = "",
    val price: Int = 0,
    val img: String = "",
    val qty: Int = 0,
    val size: Int? = null,
)

@Serializable
data class OrderDto(
    val id: Int,
    val orderNumber: String,
    val items: List<OrderItemDto> = emptyList(),
    val subtotal: Int = 0,
    val shippingCost: Int = 0,
    val discountAmount: Int = 0,
    val total: Int = 0,
    val status: String = "belum_bayar",
    val shippingAddress: String = "",
    val shippingPhone: String = "",
    val shippingPostalCode: String? = null,
    val createdAt: String = "",
    val resi: String? = null,
)

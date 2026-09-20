package com.composetemplate.core.data.network.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PaymentMethodDto(
    val code: String = "",
    val name: String = "",
    val fee: Int = 0,
    val totalFee: Int = 0,
)

@Serializable
data class PaymentMethodsResponse(
    val methods: List<PaymentMethodDto> = emptyList()
)

@Serializable
data class PaymentCreateRequest(
    val paymentMethod: String,
)

@Serializable
data class PaymentCreateResponse(
    val paymentUrl: String = "",
)

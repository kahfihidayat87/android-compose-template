package com.composetemplate.core.data.network.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentMethodDto(
    @SerialName("paymentMethod") val codeRaw: String? = null,
    @SerialName("paymentName") val nameRaw: String? = null,
    val code: String = "",
    val name: String = "",
    val fee: Int = 0,
    val totalFee: Int = 0,
) {
    val effectiveCode: String get() = code.ifEmpty { codeRaw ?: "" }
    val effectiveName: String get() = name.ifEmpty { nameRaw ?: effectiveCode }
}

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

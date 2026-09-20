package com.composetemplate.core.data.network.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ShippingRateDto(
    val courierCode: String = "",
    val courierName: String = "",
    val serviceCode: String = "",
    val serviceName: String = "",
    val price: Int = 0,
    val estimatedDays: String = "",
)

@Serializable
data class ShippingRatesResponse(
    val configured: Boolean = false,
    val rates: List<ShippingRateDto> = emptyList()
)

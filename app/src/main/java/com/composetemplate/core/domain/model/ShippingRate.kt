package com.composetemplate.core.domain.model

data class ShippingRate(
    val courierCode: String,
    val courierName: String,
    val serviceCode: String,
    val serviceName: String,
    val price: Int,
    val estimatedDays: String,
)

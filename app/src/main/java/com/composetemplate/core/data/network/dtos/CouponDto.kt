package com.composetemplate.core.data.network.dtos

import kotlinx.serialization.Serializable

@Serializable
data class CouponValidateRequest(
    val code: String,
    val subtotal: Int,
)

@Serializable
data class CouponValidateResponse(
    val valid: Boolean = false,
    val code: String = "",
    val discount: Int = 0,
    val type: String = "",
    val value: Double = 0.0,
    val error: String? = null,
)

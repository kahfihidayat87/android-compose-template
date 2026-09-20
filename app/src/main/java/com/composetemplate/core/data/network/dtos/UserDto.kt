package com.composetemplate.core.data.network.dtos

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String = "",
    val address: String = "",
    val emailVerified: Boolean = true,
)

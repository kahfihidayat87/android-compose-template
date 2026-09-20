package com.composetemplate.core.data.network.responses

import com.composetemplate.core.data.network.dtos.UserDto

data class LoginResponse(
    val success: Boolean,
    val token: String?,
    val user: UserDto?,
    val message: String?
)
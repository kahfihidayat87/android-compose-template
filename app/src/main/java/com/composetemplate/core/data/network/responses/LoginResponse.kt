package com.composetemplate.core.data.network.responses

import com.composetemplate.core.data.network.dtos.UserDto

data class LoginResponse(
    val token: String,
    val customer: UserDto,
)

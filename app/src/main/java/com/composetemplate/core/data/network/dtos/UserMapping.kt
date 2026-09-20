package com.composetemplate.core.data.network.dtos

import com.composetemplate.core.domain.model.User

fun UserDto.toUser(): User {
    return User(
        id = id,
        name = name,
        email = email,
        phone = phone,
    )
}

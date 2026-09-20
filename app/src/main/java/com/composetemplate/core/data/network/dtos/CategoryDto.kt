package com.composetemplate.core.data.network.dtos

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: Int,
    val name: String,
)

fun CategoryDto.toCategory(): com.composetemplate.core.domain.model.Category {
    return com.composetemplate.core.domain.model.Category(id = id, name = name)
}

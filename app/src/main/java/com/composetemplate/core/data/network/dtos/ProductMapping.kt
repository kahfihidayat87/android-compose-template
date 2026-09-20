package com.composetemplate.core.data.network.dtos

import com.composetemplate.core.domain.model.Product

fun ProductDto.toProduct(): Product {
    return Product(
        id = id,
        name = name,
        category = category,
        price = price,
        weight = weight,
        emoji = emoji,
        isNew = isNew,
        isBestSeller = isBestSeller,
        rating = rating,
        reviews = reviews,
        description = description,
    )
}

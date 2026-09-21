package com.composetemplate.core.data.network.dtos

import com.composetemplate.core.domain.model.Product

fun ProductDto.toProduct(): Product {
    return Product(
        id = id,
        slug = slug,
        name = name,
        category = category,
        price = price,
        oldPrice = oldPrice,
        badge = badge,
        img = img,
        sizes = sizes,
        stockBySize = stockBySize,
        weight = weight,
        desc = desc,
    )
}

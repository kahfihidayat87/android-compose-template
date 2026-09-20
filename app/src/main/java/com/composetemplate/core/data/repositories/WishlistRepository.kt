package com.composetemplate.core.data.repositories

import com.composetemplate.core.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WishlistRepository @Inject constructor() {

    private val _items = MutableStateFlow<List<Product>>(emptyList())
    val items: StateFlow<List<Product>> = _items.asStateFlow()

    fun toggle(product: Product) {
        val current = _items.value.toMutableList()
        val idx = current.indexOfFirst { it.id == product.id }
        if (idx >= 0) current.removeAt(idx) else current.add(product)
        _items.value = current
    }

    fun isWishlisted(productId: Int): Boolean = _items.value.any { it.id == productId }
}

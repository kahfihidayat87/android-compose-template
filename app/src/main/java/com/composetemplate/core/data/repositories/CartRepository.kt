package com.composetemplate.core.data.repositories

import com.composetemplate.core.domain.model.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor() {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    fun add(item: CartItem) {
        val current = _items.value.toMutableList()
        val idx = current.indexOfFirst { it.key == item.key }
        if (idx >= 0) {
            current[idx] = current[idx].copy(quantity = current[idx].quantity + item.quantity)
        } else {
            current.add(item)
        }
        _items.value = current
    }

    fun updateQuantity(key: String, qty: Int) {
        if (qty < 1) return
        _items.value = _items.value.map { if (it.key == key) it.copy(quantity = qty) else it }
    }

    fun remove(key: String) {
        _items.value = _items.value.filterNot { it.key == key }
    }

    fun clear() {
        _items.value = emptyList()
    }

    val totalItems: Int get() = _items.value.sumOf { it.quantity }
    val totalPrice: Int get() = _items.value.sumOf { it.subtotal }
}

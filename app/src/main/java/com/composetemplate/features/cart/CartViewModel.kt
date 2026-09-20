package com.composetemplate.features.cart

import androidx.lifecycle.ViewModel
import com.composetemplate.core.data.repositories.CartRepository
import com.composetemplate.core.domain.model.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    val items: StateFlow<List<CartItem>> = cartRepository.items

    fun updateQuantity(key: String, qty: Int) = cartRepository.updateQuantity(key, qty)
    fun remove(key: String) = cartRepository.remove(key)
    fun clear() = cartRepository.clear()

    fun totalPrice(items: List<CartItem>): Int = items.sumOf { it.subtotal }
    fun totalItems(items: List<CartItem>): Int = items.sumOf { it.quantity }
}

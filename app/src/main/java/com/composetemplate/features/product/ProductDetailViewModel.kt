package com.composetemplate.features.product

import androidx.lifecycle.ViewModel
import com.composetemplate.arch.extensions.LoadingAware
import com.composetemplate.arch.extensions.ViewErrorAware
import com.composetemplate.arch.extensions.collectFlow
import com.composetemplate.core.data.repositories.CartRepository
import com.composetemplate.core.domain.model.CartItem
import com.composetemplate.core.domain.model.Product
import com.composetemplate.core.usecases.product.GetProductDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val cartRepository: CartRepository
) : ViewModel(), ViewErrorAware, LoadingAware {

    val product = MutableStateFlow<Product?>(null)
    val selectedSize = MutableStateFlow<Int?>(null)
    val quantity = MutableStateFlow(1)
    val addedToCart = MutableStateFlow(false)

    fun load(id: Int) {
        collectFlow(getProductDetailUseCase(id)) {
            product.value = it
            if (selectedSize.value == null) {
                selectedSize.value = it.sizes.firstOrNull()
            }
        }
    }

    fun onSizeSelected(size: Int) {
        selectedSize.value = size
    }

    fun onQuantityChanged(qty: Int) {
        if (qty >= 1 && qty <= 10) quantity.value = qty
    }

    fun addToCart() {
        val p = product.value ?: return
        val size = selectedSize.value ?: return
        cartRepository.add(
            CartItem(
                productId = p.id,
                name = p.name,
                price = p.price,
                img = p.img,
                size = size,
                quantity = quantity.value,
            )
        )
        addedToCart.value = true
    }

    fun consumeAddedFlag() {
        addedToCart.value = false
    }
}

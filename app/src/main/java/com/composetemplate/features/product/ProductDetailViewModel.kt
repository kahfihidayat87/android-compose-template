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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val cartRepository: CartRepository
) : ViewModel(), ViewErrorAware, LoadingAware {

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product.asStateFlow()

    private val _selectedSize = MutableStateFlow<Int?>(null)
    val selectedSize: StateFlow<Int?> = _selectedSize.asStateFlow()

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()

    private val _maxQuantity = MutableStateFlow(10)
    val maxQuantity: StateFlow<Int> = _maxQuantity.asStateFlow()

    private val _addedToCart = MutableStateFlow(false)
    val addedToCart: StateFlow<Boolean> = _addedToCart.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun load(id: Int) {
        collectFlow(getProductDetailUseCase(id)) { product ->
            _product.value = product
            if (_selectedSize.value == null) {
                // Pilih ukuran pertama yang masih ada stok
                val firstAvailable = product.sizes.firstOrNull { product.hasStock(it) }
                _selectedSize.value = firstAvailable
                updateMaxQuantity(product, firstAvailable)
            }
        }
    }

    fun onSizeSelected(size: Int) {
        val p = _product.value ?: return
        if (!p.hasStock(size)) {
            _errorMessage.value = "Ukuran $size sudah habis"
            return
        }
        _selectedSize.value = size
        updateMaxQuantity(p, size)
        _errorMessage.value = null
    }

    private fun updateMaxQuantity(product: Product, size: Int?) {
        val stock = if (size != null) product.stockOf(size) else 0
        _maxQuantity.value = stock.coerceAtMost(10).coerceAtLeast(1)
        // Reset qty kalau melebihi stok
        if (_quantity.value > _maxQuantity.value) {
            _quantity.value = _maxQuantity.value
        }
    }

    fun onQuantityChanged(qty: Int) {
        val max = _maxQuantity.value
        if (qty < 1 || qty > max) return
        _quantity.value = qty
    }

    fun addToCart() {
        val p = _product.value ?: return
        val size = _selectedSize.value ?: run {
            _errorMessage.value = "Pilih ukuran dulu"
            return
        }
        if (!p.hasStock(size)) {
            _errorMessage.value = "Ukuran $size sudah habis"
            return
        }
        val qty = _quantity.value
        if (qty > p.stockOf(size)) {
            _errorMessage.value = "Stok ukuran $size tinggal ${p.stockOf(size)}"
            return
        }

        cartRepository.add(
            CartItem(
                productId = p.id,
                name = p.name,
                price = p.price,
                img = p.img,
                size = size,
                quantity = qty,
            )
        )
        _addedToCart.value = true
    }

    fun consumeAddedFlag() {
        _addedToCart.value = false
    }

    fun consumeError() {
        _errorMessage.value = null
    }
}

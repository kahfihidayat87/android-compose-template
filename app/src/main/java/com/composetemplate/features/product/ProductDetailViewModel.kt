package com.composetemplate.features.product

import androidx.lifecycle.ViewModel
import com.composetemplate.arch.extensions.LoadingAware
import com.composetemplate.arch.extensions.ViewErrorAware
import com.composetemplate.arch.extensions.collectFlow
import com.composetemplate.core.domain.model.Product
import com.composetemplate.core.usecases.product.GetProductDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductDetailUseCase: GetProductDetailUseCase
) : ViewModel(), ViewErrorAware, LoadingAware {

    val product = MutableStateFlow<Product?>(null)
    val selectedSize = MutableStateFlow(42)
    val quantity = MutableStateFlow(1)

    fun load(id: String) {
        collectFlow(getProductDetailUseCase(id)) {
            product.value = it
        }
    }

    fun onSizeSelected(size: Int) {
        selectedSize.value = size
    }

    fun onQuantityChanged(qty: Int) {
        if (qty >= 1 && qty <= 10) quantity.value = qty
    }
}

package com.composetemplate.features.home

import androidx.lifecycle.ViewModel
import com.composetemplate.arch.extensions.LoadingAware
import com.composetemplate.arch.extensions.ViewErrorAware
import com.composetemplate.arch.extensions.collectFlow
import com.composetemplate.core.domain.model.Product
import com.composetemplate.core.usecases.product.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel(), ViewErrorAware, LoadingAware {

    val products = MutableStateFlow<List<Product>>(emptyList())
    val selectedCategory = MutableStateFlow("all")

    init {
        loadProducts()
    }

    private fun loadProducts() {
        collectFlow(getProductsUseCase()) {
            products.value = it
        }
    }

    fun onCategorySelected(category: String) {
        selectedCategory.value = category
    }
}

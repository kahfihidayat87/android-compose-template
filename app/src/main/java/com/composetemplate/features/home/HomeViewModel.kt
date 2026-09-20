package com.composetemplate.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composetemplate.arch.extensions.LoadingAware
import com.composetemplate.arch.extensions.ViewErrorAware
import com.composetemplate.arch.extensions.collectFlow
import com.composetemplate.core.data.repositories.ProductRepository
import com.composetemplate.core.domain.model.Category
import com.composetemplate.core.domain.model.Product
import com.composetemplate.core.usecases.product.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val productRepository: ProductRepository,
) : ViewModel(), ViewErrorAware, LoadingAware {

    val products = MutableStateFlow<List<Product>>(emptyList())
    val categories = MutableStateFlow<List<Category>>(emptyList())
    val selectedCategory = MutableStateFlow("all")
    val searchQuery = MutableStateFlow("")

    init {
        loadProducts()
        loadCategories()
    }

    private fun loadProducts() {
        collectFlow(getProductsUseCase()) {
            products.value = it
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                categories.value = productRepository.getCategories()
            } catch (e: Exception) {
                // Fallback ke kategori default di UI
            }
        }
    }

    fun onCategorySelected(category: String) {
        selectedCategory.value = category
    }

    fun onSearchChanged(query: String) {
        searchQuery.value = query
    }
}

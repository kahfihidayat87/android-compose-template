package com.composetemplate.features.wishlist

import androidx.lifecycle.ViewModel
import com.composetemplate.core.data.repositories.WishlistRepository
import com.composetemplate.core.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val repo: WishlistRepository
) : ViewModel() {

    val items: StateFlow<List<Product>> = repo.items

    fun toggle(product: Product) = repo.toggle(product)
    fun isWishlisted(id: Int): Boolean = repo.isWishlisted(id)
}

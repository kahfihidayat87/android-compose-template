package com.composetemplate.core.usecases.product

import com.composetemplate.arch.extensions.useCaseFlow
import com.composetemplate.core.data.repositories.ProductRepository
import com.composetemplate.injection.qualifiers.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetProductDetailUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    @DefaultDispatcher private val coroutineDispatcher: CoroutineDispatcher
) {
    operator fun invoke(id: Int) = useCaseFlow(coroutineDispatcher) {
        productRepository.getProduct(id)
    }
}

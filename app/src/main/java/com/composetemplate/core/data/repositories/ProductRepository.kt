package com.composetemplate.core.data.repositories

import com.composetemplate.arch.data.Repository
import com.composetemplate.arch.extensions.repoCall
import com.composetemplate.core.data.network.Api
import com.composetemplate.core.data.network.dtos.toProduct
import com.composetemplate.core.domain.model.Product
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val api: Api
) : Repository() {

    suspend fun getProducts(): List<Product> {
        val response = repoCall {
            api.getProducts()
        }
        return response.map { it.toProduct() }
    }

    suspend fun getProduct(id: Int): Product {
        val response = repoCall {
            api.getProduct(id)
        }
        return response.toProduct()
    }
}

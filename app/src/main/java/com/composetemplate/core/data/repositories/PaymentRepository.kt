package com.composetemplate.core.data.repositories

import com.composetemplate.arch.data.Repository
import com.composetemplate.arch.extensions.repoCall
import com.composetemplate.core.data.network.Api
import com.composetemplate.core.data.network.dtos.PaymentCreateRequest
import com.composetemplate.core.data.network.dtos.PaymentCreateResponse
import com.composetemplate.core.data.network.dtos.PaymentMethodDto
import com.composetemplate.core.data.network.dtos.PaymentMethodsResponse
import javax.inject.Inject

class PaymentRepository @Inject constructor(
    private val api: Api
) : Repository() {

    suspend fun getMethods(orderId: Int): List<PaymentMethodDto> {
        val response: PaymentMethodsResponse = repoCall { api.getPaymentMethods(orderId) }
        return response.methods
    }

    suspend fun createPayment(orderId: Int, paymentMethod: String): String {
        val response: PaymentCreateResponse = repoCall {
            api.createPayment(orderId, PaymentCreateRequest(paymentMethod))
        }
        return response.paymentUrl
    }
}

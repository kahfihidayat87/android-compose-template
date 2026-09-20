package com.composetemplate.core.data.network

import com.composetemplate.core.data.network.dtos.OrderCreateRequest
import com.composetemplate.core.data.network.dtos.OrderDto
import com.composetemplate.core.data.network.dtos.PaymentCreateRequest
import com.composetemplate.core.data.network.dtos.PaymentCreateResponse
import com.composetemplate.core.data.network.dtos.PaymentMethodsResponse
import com.composetemplate.core.data.network.dtos.ProductDto
import com.composetemplate.core.data.network.dtos.ShippingRatesResponse
import com.composetemplate.core.data.network.responses.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class LoginRequest(
    val email: String,
    val password: String
)

data class ShippingItemRequest(
    val productId: Int,
    val quantity: Int
)

data class ShippingRatesRequest(
    val destinationPostalCode: String,
    val items: List<ShippingItemRequest>
)

interface Api {
    @POST("api/customer/login")
    suspend fun postLogin(@Body body: LoginRequest): Response<LoginResponse>

    @GET("api/products")
    suspend fun getProducts(): Response<List<ProductDto>>

    @GET("api/products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Response<ProductDto>

    @POST("api/shipping/rates")
    suspend fun postShippingRates(@Body body: ShippingRatesRequest): Response<ShippingRatesResponse>

    @POST("api/orders")
    suspend fun createOrder(@Body body: OrderCreateRequest): Response<OrderDto>

    @GET("api/orders/my")
    suspend fun getMyOrders(): Response<List<OrderDto>>

    @GET("api/orders/my/{id}")
    suspend fun getMyOrder(@Path("id") id: Int): Response<OrderDto>

    @GET("api/payment/{orderId}/methods")
    suspend fun getPaymentMethods(@Path("orderId") orderId: Int): Response<PaymentMethodsResponse>

    @POST("api/payment/{orderId}/create")
    suspend fun createPayment(
        @Path("orderId") orderId: Int,
        @Body body: PaymentCreateRequest
    ): Response<PaymentCreateResponse>
}

package com.composetemplate.core.data.network

import com.composetemplate.core.data.network.dtos.CategoryDto
import com.composetemplate.core.data.network.dtos.CouponValidateRequest
import com.composetemplate.core.data.network.dtos.CouponValidateResponse
import com.composetemplate.core.data.network.dtos.OrderCreateRequest
import com.composetemplate.core.data.network.dtos.OrderDto
import com.composetemplate.core.data.network.dtos.PaymentCreateRequest
import com.composetemplate.core.data.network.dtos.PaymentCreateResponse
import com.composetemplate.core.data.network.dtos.PaymentMethodsResponse
import com.composetemplate.core.data.network.dtos.PostDto
import com.composetemplate.core.data.network.dtos.ProductDto
import com.composetemplate.core.data.network.dtos.ResourceDetailsDto
import com.composetemplate.core.data.network.dtos.ResourceDto
import com.composetemplate.core.data.network.dtos.ShippingRatesResponse
import com.composetemplate.core.data.network.responses.LoginResponse
import com.composetemplate.core.data.network.responses.ResourcesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String, val phone: String)
data class ShippingItemRequest(val productId: Int, val quantity: Int)
data class ShippingRatesRequest(val destinationPostalCode: String, val items: List<ShippingItemRequest>)

interface Api {
    @POST("api/customer/login")
    suspend fun postLogin(@Body body: LoginRequest): Response<LoginResponse>

    @POST("api/customer/register")
    suspend fun postRegister(@Body body: RegisterRequest): Response<LoginResponse>

    @GET("api/products")
    suspend fun getProducts(): Response<List<ProductDto>>

    @GET("api/products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Response<ProductDto>

    @GET("api/categories")
    suspend fun getCategories(): Response<List<CategoryDto>>

    @POST("api/coupons/validate")
    suspend fun validateCoupon(@Body body: CouponValidateRequest): Response<CouponValidateResponse>

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

    @GET("posts")
    suspend fun getPosts(): Response<List<PostDto>>

    @GET("pokemon")
    suspend fun getResources(@Query("offset") page: Int, @Query("limit") limit: Int): Response<ResourcesResponse>

    @GET
    suspend fun getResourcesDetails(@Url url: String): Response<ResourceDetailsDto>
}

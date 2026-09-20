package com.composetemplate.core.data.network

import com.composetemplate.core.data.network.dtos.PostDto
import com.composetemplate.core.data.network.dtos.ProductDto
import com.composetemplate.core.data.network.dtos.ResourceDetailsDto
import com.composetemplate.core.data.network.dtos.ResourceDto
import com.composetemplate.core.data.network.responses.LoginResponse
import com.composetemplate.core.data.network.responses.ResourcesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

data class LoginRequest(
    val email: String,
    val password: String
)

interface Api {
    @POST("api/customer/login")
    suspend fun postLogin(@Body body: LoginRequest): Response<LoginResponse>

    @GET("api/products")
    suspend fun getProducts(): Response<List<ProductDto>>

    @GET("api/products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Response<ProductDto>

    @GET("posts")
    suspend fun getPosts(): Response<List<PostDto>>

    @GET("pokemon")
    suspend fun getResources(
        @Query("offset") page: Int,
        @Query("limit") limit: Int
    ): Response<ResourcesResponse>

    @GET
    suspend fun getResourcesDetails(@Url url: String): Response<ResourceDetailsDto>
}

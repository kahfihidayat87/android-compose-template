package com.composetemplate.core.data.network

import com.composetemplate.core.data.network.dtos.PostDto
import com.composetemplate.core.data.network.dtos.ProductDetailResponse
import com.composetemplate.core.data.network.dtos.ProductsResponse
import com.composetemplate.core.data.network.dtos.ResourceDetailsDto
import com.composetemplate.core.data.network.dtos.ResourceDto
import com.composetemplate.core.data.network.responses.LoginResponse
import com.composetemplate.core.data.network.responses.ResourcesResponse
import com.composetemplate.core.data.network.responses.UserResponse
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
    @POST("api/auth/login")
    suspend fun postLogin(@Body body: LoginRequest): Response<LoginResponse>

    @GET("api/products")
    suspend fun getProducts(): Response<ProductsResponse>

    @GET("api/products/{id}")
    suspend fun getProduct(@Path("id") id: String): Response<ProductDetailResponse>

    @GET("posts")
    suspend fun getPosts(): Response<List<PostDto>>

    @GET("users/2")
    suspend fun getUser(): Response<UserResponse>

    @GET("pokemon")
    suspend fun getResources(
        @Query("offset") page: Int,
        @Query("limit") limit: Int
    ): Response<ResourcesResponse>

    @GET
    suspend fun getResourcesDetails(@Url url: String): Response<ResourceDetailsDto>
}

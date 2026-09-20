package com.composetemplate.core.data.repositories

import com.composetemplate.arch.data.Repository
import com.composetemplate.arch.extensions.repoCall
import com.composetemplate.core.data.network.Api
import com.composetemplate.core.data.network.LoginRequest
import com.composetemplate.core.data.network.dtos.toUser
import com.composetemplate.core.data.network.responses.LoginResponse
import com.composetemplate.core.data.storage.UserPreferenceStore
import com.composetemplate.core.domain.model.User
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val api: Api,
    private val userPreferenceStore: UserPreferenceStore
) : Repository() {

    suspend fun login(email: String, password: String): User {
        val response: LoginResponse = repoCall {
            api.postLogin(LoginRequest(email, password))
        }
        val user: User = response.user?.toUser()
            ?: throw IllegalStateException(response.message ?: "Login gagal")
        userPreferenceStore.add(user)
        return user
    }
}
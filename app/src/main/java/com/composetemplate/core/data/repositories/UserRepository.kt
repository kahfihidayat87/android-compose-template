package com.composetemplate.core.data.repositories

import com.composetemplate.arch.data.Repository
import com.composetemplate.arch.extensions.repoCall
import com.composetemplate.core.data.network.Api
import com.composetemplate.core.data.network.LoginRequest
import com.composetemplate.core.data.network.RegisterRequest
import com.composetemplate.core.data.network.dtos.toUser
import com.composetemplate.core.data.network.responses.LoginResponse
import com.composetemplate.core.data.storage.TokenManager
import com.composetemplate.core.data.storage.UserPreferenceStore
import com.composetemplate.core.domain.model.User
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val api: Api,
    private val userPreferenceStore: UserPreferenceStore,
    private val tokenManager: TokenManager
) : Repository() {

    suspend fun login(email: String, password: String): User {
        val response: LoginResponse = repoCall {
            api.postLogin(LoginRequest(email, password))
        }
        val user = response.customer.toUser()
        userPreferenceStore.add(user)
        tokenManager.saveToken(response.token)
        return user
    }

    suspend fun register(name: String, email: String, password: String, phone: String): User {
        val response: LoginResponse = repoCall {
            api.postRegister(RegisterRequest(name, email, password, phone))
        }
        val user = response.customer.toUser()
        userPreferenceStore.add(user)
        tokenManager.saveToken(response.token)
        return user
    }

    suspend fun logout() {
        tokenManager.clear()
    }

    suspend fun isLoggedIn(): Boolean = !tokenManager.getToken().isNullOrEmpty()
}

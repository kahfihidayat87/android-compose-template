package com.composetemplate.core.data.repositories

import com.composetemplate.arch.data.Repository
import com.composetemplate.arch.extensions.repoCall
import com.composetemplate.core.data.network.Api
import com.composetemplate.core.data.network.dtos.OnboardingResponse
import com.composetemplate.core.data.network.dtos.toOnboardingPage
import com.composetemplate.core.domain.model.OnboardingPage
import javax.inject.Inject

class OnboardingRepository @Inject constructor(
    private val api: Api
) : Repository() {

    suspend fun getPages(): List<OnboardingPage> {
        val response: OnboardingResponse = repoCall { api.getOnboarding() }
        return response.pages.map { it.toOnboardingPage() }
    }
}

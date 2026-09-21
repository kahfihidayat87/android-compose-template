package com.composetemplate.core.data.network.dtos

import kotlinx.serialization.Serializable

@Serializable
data class OnboardingPageDto(
    val id: Int = 0,
    val emoji: String = "",
    val title: String = "",
    val description: String = "",
)

@Serializable
data class OnboardingResponse(
    val pages: List<OnboardingPageDto> = emptyList(),
)

fun OnboardingPageDto.toOnboardingPage(): com.composetemplate.core.domain.model.OnboardingPage {
    return com.composetemplate.core.domain.model.OnboardingPage(
        id = id,
        emoji = emoji,
        title = title,
        description = description,
    )
}

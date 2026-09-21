package com.composetemplate.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class OnboardingPage(
    val id: Int,
    val emoji: String,
    val title: String,
    val description: String,
)

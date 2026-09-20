package com.composetemplate.core.navigation.wakaf

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.composetemplate.features.wakaf.WakafScreen

const val wakafNavigationRoute = "wakaf_route"

fun NavController.navigateToWakaf(navOptions: NavOptions? = null) {
    this.navigate(wakafNavigationRoute, navOptions)
}

fun NavGraphBuilder.wakafScreen() {
    composable(route = wakafNavigationRoute) {
        WakafScreen()
    }
}

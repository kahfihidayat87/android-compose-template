package com.composetemplate.core.navigation.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.composetemplate.features.home.HomeRoute

const val homeNavigationRoute = "home_route"

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    this.navigate(homeNavigationRoute, navOptions)
}

fun NavGraphBuilder.homeScreen(
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onWishlistClick: () -> Unit
) {
    composable(route = homeNavigationRoute) {
        HomeRoute(
            onProductClick = onProductClick,
            onCartClick = onCartClick,
            onWishlistClick = onWishlistClick
        )
    }
}

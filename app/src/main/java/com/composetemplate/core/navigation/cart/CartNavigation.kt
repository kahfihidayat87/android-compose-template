package com.composetemplate.core.navigation.cart

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.composetemplate.features.cart.CartRoute

const val cartNavigationRoute = "cart_route"

fun NavController.navigateToCart(navOptions: NavOptions? = null) {
    this.navigate(cartNavigationRoute, navOptions)
}

fun NavGraphBuilder.cartScreen(
    onBackClick: () -> Unit,
    onCheckoutClick: () -> Unit
) {
    composable(route = cartNavigationRoute) {
        CartRoute(
            onBackClick = onBackClick,
            onCheckoutClick = onCheckoutClick
        )
    }
}

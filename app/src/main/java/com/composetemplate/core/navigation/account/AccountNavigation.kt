package com.composetemplate.core.navigation.account

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.composetemplate.core.navigation.cart.navigateToCart
import com.composetemplate.core.navigation.home.navigateToHome
import com.composetemplate.core.navigation.orders.navigateToOrders
import com.composetemplate.core.navigation.wishlist.navigateToWishlist
import com.composetemplate.features.account.AccountRoute

const val accountNavigationRoute = "account_route"

fun NavController.navigateToAccount(navOptions: NavOptions? = null) {
    this.navigate(accountNavigationRoute, navOptions)
}

fun NavGraphBuilder.accountScreen(
    onLoggedOut: () -> Unit,
    onCartClick: () -> Unit,
    onWishlistClick: () -> Unit,
    onOrdersClick: () -> Unit,
) {
    composable(route = accountNavigationRoute) {
        AccountRoute(
            onLoggedOut = onLoggedOut,
            onCartClick = onCartClick,
            onWishlistClick = onWishlistClick,
            onOrdersClick = onOrdersClick,
        )
    }
}

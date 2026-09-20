package com.composetemplate.core.navigation.wishlist

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.composetemplate.features.wishlist.WishlistRoute

const val wishlistNavigationRoute = "wishlist_route"

fun NavController.navigateToWishlist(navOptions: NavOptions? = null) {
    this.navigate(wishlistNavigationRoute, navOptions)
}

fun NavGraphBuilder.wishlistScreen(onProductClick: (Int) -> Unit) {
    composable(route = wishlistNavigationRoute) {
        WishlistRoute(onProductClick = onProductClick)
    }
}

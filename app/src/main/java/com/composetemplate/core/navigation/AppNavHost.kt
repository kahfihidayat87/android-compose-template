package com.composetemplate.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.composetemplate.core.navigation.account.accountScreen
import com.composetemplate.core.navigation.cart.cartScreen
import com.composetemplate.core.navigation.cart.navigateToCart
import com.composetemplate.core.navigation.checkout.checkoutScreen
import com.composetemplate.core.navigation.checkout.navigateToCheckout
import com.composetemplate.core.navigation.checkout.navigateToPaymentWebView
import com.composetemplate.core.navigation.checkout.paymentWebViewScreen
import com.composetemplate.core.navigation.home.homeNavigationRoute
import com.composetemplate.core.navigation.home.homeScreen
import com.composetemplate.core.navigation.login.loginNavigationRoute
import com.composetemplate.core.navigation.login.loginScreen
import com.composetemplate.core.navigation.login.navigateToRegister
import com.composetemplate.core.navigation.login.registerScreen
import com.composetemplate.core.navigation.orders.navigateToOrderDetail
import com.composetemplate.core.navigation.orders.orderDetailScreen
import com.composetemplate.core.navigation.orders.ordersScreen
import com.composetemplate.core.navigation.product.navigateToProductDetail
import com.composetemplate.core.navigation.product.productDetailScreen
import com.composetemplate.core.navigation.resource.navigateToResourceDetails
import com.composetemplate.core.navigation.resource.resourceDetailsScreen
import com.composetemplate.core.navigation.resource.resourcesGraph
import com.composetemplate.core.navigation.sizeguide.navigateToSizeGuide
import com.composetemplate.core.navigation.sizeguide.sizeGuideScreen
import com.composetemplate.core.navigation.wakaf.wakafScreen
import com.composetemplate.core.navigation.wishlist.navigateToWishlist
import com.composetemplate.core.navigation.wishlist.wishlistScreen


@Composable
fun AppNavHost(
    navController: NavHostController,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    startDestination: String = loginNavigationRoute
) {
    val navigateToHomeClearingLogin: () -> Unit = {
        navController.navigate(homeNavigationRoute) {
            popUpTo(loginNavigationRoute) { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        homeScreen(
            onProductClick = { productId ->
                navController.navigateToProductDetail(productId)
            },
            onCartClick = { navController.navigateToCart() },
            onWishlistClick = { navController.navigateToWishlist() },
            onAccountClick = {
                navController.navigate(com.composetemplate.core.navigation.account.accountNavigationRoute)
            }
        )
        loginScreen(
            navigateToHome = navigateToHomeClearingLogin,
            navigateToRegister = { navController.navigateToRegister() }
        )
        registerScreen(
            onRegisterSuccess = navigateToHomeClearingLogin,
            onBackClick = onBackClick
        )
        productDetailScreen(
            onBackClick = onBackClick,
            onSizeGuideClick = { navController.navigateToSizeGuide() }
        )
        cartScreen(
            onBackClick = onBackClick,
            onCheckoutClick = { navController.navigateToCheckout() }
        )
        checkoutScreen(
            onBackClick = onBackClick,
            onPaymentUrl = { url -> navController.navigateToPaymentWebView(url) }
        )
        paymentWebViewScreen(onBack = {
            navController.popBackStack(route = homeNavigationRoute, inclusive = false)
        })
        ordersScreen(onOrderClick = { orderId ->
            navController.navigateToOrderDetail(orderId)
        })
        orderDetailScreen(onBackClick = onBackClick)
        wishlistScreen(onProductClick = { productId ->
            navController.navigateToProductDetail(productId)
        })
        sizeGuideScreen(onBackClick = onBackClick)
        wakafScreen()
        accountScreen(
            onLoggedOut = {
                navController.navigate(loginNavigationRoute) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onCartClick = { navController.navigateToCart() },
            onWishlistClick = { navController.navigateToWishlist() },
            onOrdersClick = { navController.navigate(com.composetemplate.core.navigation.orders.ordersNavigationRoute) }
        )
        resourcesGraph(
            navController = navController,
            onItemClick = { navController.navigateToResourceDetails(it) },
            nestedGraphs = {
                resourceDetailsScreen(navController, onBackClick)
            }
        )
    }
}

package com.composetemplate.core.navigation.checkout

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.composetemplate.features.checkout.CheckoutRoute
import com.composetemplate.features.checkout.PaymentWebViewScreen

const val checkoutNavigationRoute = "checkout_route"
const val paymentWebViewRoute = "payment_webview_route/{url}"
const val paymentUrlArg = "url"

fun NavController.navigateToCheckout(navOptions: NavOptions? = null) {
    this.navigate(checkoutNavigationRoute, navOptions)
}

fun NavController.navigateToPaymentWebView(url: String) {
    val encoded = java.net.URLEncoder.encode(url, "UTF-8")
    this.navigate("payment_webview_route/$encoded")
}

fun NavGraphBuilder.checkoutScreen(
    onBackClick: () -> Unit,
    onPaymentUrl: (String) -> Unit
) {
    composable(route = checkoutNavigationRoute) {
        CheckoutRoute(
            onBackClick = onBackClick,
            onPaymentUrl = onPaymentUrl
        )
    }
}

fun NavGraphBuilder.paymentWebViewScreen(onBack: () -> Unit) {
    composable(
        route = paymentWebViewRoute,
        arguments = listOf(navArgument(paymentUrlArg) { type = NavType.StringType })
    ) { backStackEntry ->
        val encoded = backStackEntry.arguments?.getString(paymentUrlArg).orEmpty()
        val url = java.net.URLDecoder.decode(encoded, "UTF-8")
        PaymentWebViewScreen(url = url, onBack = onBack)
    }
}

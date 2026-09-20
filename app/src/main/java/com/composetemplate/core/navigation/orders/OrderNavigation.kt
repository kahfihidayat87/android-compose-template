package com.composetemplate.core.navigation.orders

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.composetemplate.features.orders.OrderDetailRoute
import com.composetemplate.features.orders.OrderHistoryRoute

const val ordersNavigationRoute = "orders_route"
const val orderDetailRoute = "order_detail_route/{orderId}"
const val orderIdArg = "orderId"

fun NavController.navigateToOrders(navOptions: NavOptions? = null) {
    this.navigate(ordersNavigationRoute, navOptions)
}

fun NavController.navigateToOrderDetail(orderId: Int) {
    this.navigate("order_detail_route/$orderId")
}

fun NavGraphBuilder.ordersScreen(onOrderClick: (Int) -> Unit) {
    composable(route = ordersNavigationRoute) {
        OrderHistoryRoute(onOrderClick = onOrderClick)
    }
}

fun NavGraphBuilder.orderDetailScreen(onBackClick: () -> Unit) {
    composable(
        route = orderDetailRoute,
        arguments = listOf(navArgument(orderIdArg) { type = NavType.IntType })
    ) { backStackEntry ->
        val orderId = backStackEntry.arguments?.getInt(orderIdArg) ?: 0
        OrderDetailRoute(
            orderId = orderId,
            onBackClick = onBackClick
        )
    }
}

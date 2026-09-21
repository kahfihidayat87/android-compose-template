package com.composetemplate.core.navigation.product

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.composetemplate.features.product.ProductDetailRoute

const val productIdArg = "productId"
internal const val productDetailRoute = "product_detail_route"
internal const val productDetailNavigationRoute = "$productDetailRoute/{$productIdArg}"

fun NavController.navigateToProductDetail(productId: Int) {
    this.navigate("$productDetailRoute/$productId")
}

fun NavGraphBuilder.productDetailScreen(
    onBackClick: () -> Unit,
    onSizeGuideClick: () -> Unit,
    onCartClick: () -> Unit
) {
    composable(
        route = productDetailNavigationRoute,
        arguments = listOf(
            navArgument(productIdArg) { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val productId = backStackEntry.arguments?.getString(productIdArg).orEmpty()
        ProductDetailRoute(
            productId = productId,
            onBackClick = onBackClick,
            onSizeGuideClick = onSizeGuideClick,
            onCartClick = onCartClick
        )
    }
}

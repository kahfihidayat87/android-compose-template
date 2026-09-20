package com.composetemplate.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.composetemplate.core.navigation.home.homeScreen
import com.composetemplate.core.navigation.home.navigateToHome
import com.composetemplate.core.navigation.login.loginNavigationRoute
import com.composetemplate.core.navigation.login.loginScreen
import com.composetemplate.core.navigation.product.navigateToProductDetail
import com.composetemplate.core.navigation.product.productDetailScreen
import com.composetemplate.core.navigation.resource.navigateToResourceDetails
import com.composetemplate.core.navigation.resource.resourceDetailsScreen
import com.composetemplate.core.navigation.resource.resourcesGraph


@Composable
fun AppNavHost(
    navController: NavHostController,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    startDestination: String = loginNavigationRoute
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        homeScreen(onProductClick = { productId ->
            navController.navigateToProductDetail(productId)
        })
        loginScreen(navigateToHome = { navController.navigateToHome() })
        productDetailScreen(onBackClick = onBackClick)
        resourcesGraph(
            navController = navController,
            onItemClick = { navController.navigateToResourceDetails(it) },
            nestedGraphs = {
                resourceDetailsScreen(navController, onBackClick)
            }
        )
    }
}

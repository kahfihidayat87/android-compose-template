package com.composetemplate.core.navigation.sizeguide

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.composetemplate.features.sizeguide.SizeGuideScreen

const val sizeGuideRoute = "size_guide_route"

fun NavController.navigateToSizeGuide(navOptions: NavOptions? = null) {
    this.navigate(sizeGuideRoute, navOptions)
}

fun NavGraphBuilder.sizeGuideScreen(onBackClick: () -> Unit) {
    composable(route = sizeGuideRoute) {
        SizeGuideScreen(onBackClick = onBackClick)
    }
}

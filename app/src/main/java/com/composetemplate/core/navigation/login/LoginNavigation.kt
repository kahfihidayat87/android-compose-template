package com.composetemplate.core.navigation.login

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.composetemplate.features.login.LoginRoute
import com.composetemplate.features.login.RegisterRoute

const val loginNavigationRoute = "login_route"
const val registerNavigationRoute = "register_route"

fun NavController.navigateToLogin(navOptions: NavOptions? = null) {
    this.navigate(loginNavigationRoute, navOptions)
}

fun NavController.navigateToRegister(navOptions: NavOptions? = null) {
    this.navigate(registerNavigationRoute, navOptions)
}

fun NavGraphBuilder.loginScreen(
    navigateToHome: () -> Unit,
    navigateToRegister: () -> Unit
) {
    composable(route = loginNavigationRoute) {
        LoginRoute(
            navigateToHome = navigateToHome,
            navigateToRegister = navigateToRegister
        )
    }
}

fun NavGraphBuilder.registerScreen(
    onRegisterSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    composable(route = registerNavigationRoute) {
        RegisterRoute(
            onRegisterSuccess = onRegisterSuccess,
            onBackClick = onBackClick
        )
    }
}

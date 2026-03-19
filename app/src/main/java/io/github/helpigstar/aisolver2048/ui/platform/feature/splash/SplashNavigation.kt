package io.github.helpigstar.aisolver2048.ui.platform.feature.splash

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object SplashRoute

fun NavGraphBuilder.splashDestination() {
    composable<SplashRoute> { SplashScreen() }
}

fun NavController.navigateToSpalsh(
    navOptions: NavOptions? = null,
) {
    navigate(SplashRoute, navOptions)
}
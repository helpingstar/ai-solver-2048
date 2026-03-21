package io.github.helpigstar.aisolver2048.ui.platform.feature.rootnav

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object RootNavigationRoute

fun NavGraphBuilder.rootNavDestination(
    onSplashScreenRemoved: () -> Unit,
) {
    composable<RootNavigationRoute> {
        RootNavScreen(onSplashScreenRemoved = onSplashScreenRemoved)
    }
}
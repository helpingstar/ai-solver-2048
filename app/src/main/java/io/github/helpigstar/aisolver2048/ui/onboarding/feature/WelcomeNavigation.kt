package io.github.helpigstar.aisolver2048.ui.onboarding.feature

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object WelcomeRoute


fun NavController.navigateToWelcome(navOptions: NavOptions? = null) {
    this.navigate(route = WelcomeRoute, navOptions = navOptions)
}

fun NavGraphBuilder.welcomeDestination() {
    composable<WelcomeRoute> {
        WelcomeScreen()
    }
}
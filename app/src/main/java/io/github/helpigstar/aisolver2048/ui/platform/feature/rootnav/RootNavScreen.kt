package io.github.helpigstar.aisolver2048.ui.platform.feature.rootnav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import io.github.helpigstar.aisolver2048.ui.onboarding.feature.navigateToWelcome
import io.github.helpigstar.aisolver2048.ui.onboarding.feature.welcomeDestination
import io.github.helpigstar.aisolver2048.ui.platform.components.util.rememberAisolver2048NavController
import io.github.helpigstar.aisolver2048.ui.platform.feature.splash.SplashRoute
import io.github.helpigstar.aisolver2048.ui.platform.feature.splash.splashDestination
import io.github.helpigstar.aisolver2048.ui.workspace.feature.workspace.navigateToWorkspace
import io.github.helpigstar.aisolver2048.ui.workspace.feature.workspace.workspaceDestination

@Composable
fun RootNavScreen(
    viewModel: RootNavViewModel = hiltViewModel(),
    navController: NavHostController = rememberAisolver2048NavController(name = "RootNavScreen"),
    onSplashScreenRemoved: () -> Unit = {},
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    val isNotSplashScreen = state != RootNavState.Splash
    LaunchedEffect(isNotSplashScreen) {
        if (isNotSplashScreen) {
            onSplashScreenRemoved()
        }
    }

    NavHost(
        navController = navController,
        startDestination = SplashRoute
    ) {
        splashDestination()
        welcomeDestination()
        workspaceDestination()
    }

    val rootNavOptions = navOptions {
        popUpTo(navController.graph.id) {
            inclusive = false
            saveState = false
        }
        launchSingleTop = true
        restoreState = false
    }

    LaunchedEffect(state) {
        when (state) {
            RootNavState.Splash -> Unit

            RootNavState.Onboarding -> {
                navController.navigateToWelcome(navOptions = rootNavOptions)
            }

            RootNavState.Workspace -> {
                navController.navigateToWorkspace(navOptions = rootNavOptions)
            }
        }
    }
}
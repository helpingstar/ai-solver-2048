package io.github.helpigstar.aisolver2048.ui.platform.feature.rootnav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.github.helpigstar.aisolver2048.ui.onboarding.feature.welcomeDestination
import io.github.helpigstar.aisolver2048.ui.platform.components.util.rememberAisolver2048NavController
import io.github.helpigstar.aisolver2048.ui.platform.feature.splash.SplashRoute
import io.github.helpigstar.aisolver2048.ui.platform.feature.splash.splashDestination
import io.github.helpigstar.aisolver2048.ui.workspace.feature.workspace.workspaceDestination

@Composable
fun RootNavScreen(
    viewModel: RootNavViewModel = hiltViewModel(),
    navController: NavHostController = rememberAisolver2048NavController(name = "RootNavScreen"),
    onSplashScreenRemoved: () -> Unit = {},
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = SplashRoute
    ) {
        splashDestination()
        welcomeDestination()
        workspaceDestination()
    }
}
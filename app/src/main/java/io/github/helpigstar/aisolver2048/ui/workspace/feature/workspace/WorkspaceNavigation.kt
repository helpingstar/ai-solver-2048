package io.github.helpigstar.aisolver2048.ui.workspace.feature.workspace

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object WorkspaceRoute

fun NavController.navigateToWorkspace(
    navOptions: NavOptions? = null,
) {
    navigate(route = WorkspaceRoute, navOptions = navOptions)
}

fun NavGraphBuilder.workspaceDestination() {
    composable<WorkspaceRoute> {
        WorkspaceScreen()
    }
}
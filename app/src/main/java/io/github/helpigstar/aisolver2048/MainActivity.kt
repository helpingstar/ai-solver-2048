package io.github.helpigstar.aisolver2048

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import dagger.hilt.android.AndroidEntryPoint
import io.github.helpigstar.aisolver2048.ui.platform.components.util.rememberAisolver2048NavController
import io.github.helpigstar.aisolver2048.ui.platform.feature.rootnav.RootNavigationRoute
import io.github.helpigstar.aisolver2048.ui.platform.feature.rootnav.rootNavDestination
import io.github.helpigstar.aisolver2048.ui.theme.AiSolver2048Theme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var shouldShowSplashScreen = true
        installSplashScreen().setKeepOnScreenCondition { shouldShowSplashScreen }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberAisolver2048NavController(name = "MainActivity")
            AiSolver2048Theme {
                NavHost(
                    navController = navController,
                    startDestination = RootNavigationRoute,
                    modifier = Modifier
                        .background(color = Color.White)
                ) {
                    rootNavDestination { shouldShowSplashScreen = false }
                }
            }
        }
    }
}

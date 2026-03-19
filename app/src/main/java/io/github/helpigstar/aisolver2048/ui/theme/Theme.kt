package io.github.helpigstar.aisolver2048.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AisolverColorScheme = lightColorScheme(
    primary = AisolverActionBrown,
    onPrimary = AisolverBrightText,
    secondary = AisolverGold,
    onSecondary = AisolverBrightText,
    tertiary = AisolverBoardBackground,
    onTertiary = AisolverBrightText,
    background = AisolverPageBackground,
    onBackground = AisolverPrimaryText,
    surface = AisolverPageBackground,
    onSurface = AisolverPrimaryText,
    surfaceVariant = AisolverBaseTile,
    onSurfaceVariant = AisolverPrimaryText,
    primaryContainer = AisolverBoardBackground,
    onPrimaryContainer = AisolverBrightText,
    secondaryContainer = AisolverGold.copy(alpha = 0.18f),
    onSecondaryContainer = AisolverPrimaryText
)

@Composable
fun AiSolver2048Theme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = AisolverColorScheme,
        typography = Typography,
        content = content
    )
}

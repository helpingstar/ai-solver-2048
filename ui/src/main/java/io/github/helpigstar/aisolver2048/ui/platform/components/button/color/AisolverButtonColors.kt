package io.github.helpigstar.aisolver2048.ui.platform.components.button.color

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.helpigstar.aisolver2048.ui.platform.theme.color.defaultAisolverColorScheme



private val aisolverTonalIconButtonContainerColor = Color(0xFFDBE4E7)
private val aisolverTonalIconButtonContentColor = Color(0xFF1F1F1F)
private const val AISOLVER_TONAL_ICON_BUTTON_DISABLED_ALPHA = 0.3f


@Composable
fun aisolverFilledButtonColors(): ButtonColors =
    ButtonDefaults.buttonColors(
        containerColor = Color(0xFFC55A3A),
        contentColor = Color.White,
        disabledContainerColor = defaultAisolverColorScheme.button.primaryBackgroundDisabled,
        disabledContentColor = defaultAisolverColorScheme.button.primaryForegroundDisabled,
    )

@Composable
fun aisolverAnalyzeFilledButtonColors(): ButtonColors =
    ButtonDefaults.buttonColors(
        containerColor = defaultAisolverColorScheme.button.primaryBackground,
        contentColor = defaultAisolverColorScheme.button.primaryForeground,
        disabledContainerColor = aisolverTonalIconButtonContainerColor.copy(
            alpha = AISOLVER_TONAL_ICON_BUTTON_DISABLED_ALPHA,
        ),
        disabledContentColor = aisolverTonalIconButtonContentColor.copy(
            alpha = AISOLVER_TONAL_ICON_BUTTON_DISABLED_ALPHA,
        ),
    )

@Composable
fun aisolverAutoFilledButtonColors(): ButtonColors =
    ButtonDefaults.buttonColors(
        containerColor = defaultAisolverColorScheme.button.autoAnalyzeBackground,
        contentColor = defaultAisolverColorScheme.button.autoAnalyzeForeground,
        disabledContainerColor = aisolverTonalIconButtonContainerColor.copy(
            alpha = AISOLVER_TONAL_ICON_BUTTON_DISABLED_ALPHA,
        ),
        disabledContentColor = aisolverTonalIconButtonContentColor.copy(
            alpha = AISOLVER_TONAL_ICON_BUTTON_DISABLED_ALPHA,
        ),
    )

@Composable
fun aisolverStopFilledButtonColors(): ButtonColors =
    ButtonDefaults.buttonColors(
        containerColor = defaultAisolverColorScheme.button.stopBackground,
        contentColor = defaultAisolverColorScheme.button.stopForeground,
        disabledContainerColor = defaultAisolverColorScheme.button.primaryBackgroundDisabled,
        disabledContentColor = defaultAisolverColorScheme.button.primaryForegroundDisabled,
    )

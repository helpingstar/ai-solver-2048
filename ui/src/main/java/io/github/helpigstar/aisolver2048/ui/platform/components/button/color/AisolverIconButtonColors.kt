package io.github.helpigstar.aisolver2048.ui.platform.components.button.color

import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val aisolverTonalIconButtonContainerColor = Color(0xFFDBE4E7)
private val aisolverTonalIconButtonContentColor = Color(0xFF1F1F1F)
private const val AISOLVER_TONAL_ICON_BUTTON_DISABLED_ALPHA = 0.3f

@Composable
fun aisolverTonalIconButtonColors(): IconButtonColors =
    IconButtonDefaults.filledIconButtonColors(
        containerColor = aisolverTonalIconButtonContainerColor,
        contentColor = aisolverTonalIconButtonContentColor,
        disabledContainerColor = aisolverTonalIconButtonContainerColor.copy(
            alpha = AISOLVER_TONAL_ICON_BUTTON_DISABLED_ALPHA,
        ),
        disabledContentColor = aisolverTonalIconButtonContentColor.copy(
            alpha = AISOLVER_TONAL_ICON_BUTTON_DISABLED_ALPHA,
        ),
    )

package io.github.helpigstar.aisolver2048.ui.platform.components.foundation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AisolverPreviewTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AisolverLightColorScheme,
        typography = AisolverTypography,
        content = content
    )
}

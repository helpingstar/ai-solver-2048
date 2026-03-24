package io.github.helpigstar.aisolver2048.ui.platform.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.helpigstar.aisolver2048.ui.platform.components.button.AisolverActionButton
import io.github.helpigstar.aisolver2048.ui.platform.components.button.AisolverActionButtonVariant

object AisolverGameActionsDefaults {
    val ContentSpacing = 16.dp
}

@Composable
fun AisolverGameActions(
    onUndoClick: () -> Unit,
    onResetClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    undoEnabled: Boolean = true,
    resetEnabled: Boolean = true,
    settingsEnabled: Boolean = true,
    undoContentDescription: String? = null,
    resetContentDescription: String? = null,
    settingsContentDescription: String? = null,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AisolverGameActionsDefaults.ContentSpacing),
    ) {
        AisolverActionButton(
            variant = AisolverActionButtonVariant.Undo,
            onClick = onUndoClick,
            enabled = undoEnabled,
            contentDescription = undoContentDescription ?: AisolverActionButtonVariant.Undo.label,
        )
        AisolverActionButton(
            variant = AisolverActionButtonVariant.Reset,
            onClick = onResetClick,
            enabled = resetEnabled,
            contentDescription = resetContentDescription ?: AisolverActionButtonVariant.Reset.label,
        )
        AisolverActionButton(
            variant = AisolverActionButtonVariant.Settings,
            onClick = onSettingsClick,
            enabled = settingsEnabled,
            contentDescription = settingsContentDescription ?: AisolverActionButtonVariant.Settings.label,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AisolverGameActionsPreview() {
    MaterialTheme {
        AisolverGameActions(
            onUndoClick = {},
            onResetClick = {},
            onSettingsClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

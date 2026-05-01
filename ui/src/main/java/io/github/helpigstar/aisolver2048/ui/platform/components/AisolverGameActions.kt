package io.github.helpigstar.aisolver2048.ui.platform.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.helpigstar.aisolver2048.ui.platform.components.button.AisolverTonalIconButton
import io.github.helpigstar.aisolver2048.ui.platform.resource.AisolverDrawable
import io.github.helpigstar.aisolver2048.ui.platform.resource.AisolverString

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
        AisolverTonalIconButton(
            vectorIconRes = AisolverDrawable.ic_undo,
            contentDescription = undoContentDescription ?: stringResource(AisolverString.action_undo),
            onClick = onUndoClick,
            isEnabled = undoEnabled,
        )
        AisolverTonalIconButton(
            vectorIconRes = AisolverDrawable.ic_autorenew,
            contentDescription = resetContentDescription ?: stringResource(AisolverString.action_reset),
            onClick = onResetClick,
            isEnabled = resetEnabled,
        )
        AisolverTonalIconButton(
            vectorIconRes = AisolverDrawable.ic_settings,
            contentDescription = settingsContentDescription
                ?: stringResource(AisolverString.action_settings),
            onClick = onSettingsClick,
            isEnabled = settingsEnabled,
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

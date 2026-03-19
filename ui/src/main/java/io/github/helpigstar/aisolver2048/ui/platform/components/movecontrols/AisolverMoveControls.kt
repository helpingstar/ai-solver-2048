package io.github.helpigstar.aisolver2048.ui.platform.components.movecontrols

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.helpigstar.aisolver2048.ui.platform.components.actionbutton.AisolverActionButton
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverPreviewTheme
import io.github.helpigstar.aisolver2048.ui.platform.components.model.AisolverMoveDirection

@Composable
fun AisolverMoveControls(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    compact: Boolean = false,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MoveButton(
                direction = AisolverMoveDirection.Up,
                compact = compact,
                enabled = enabled,
                modifier = Modifier.weight(1f)
            )
            MoveButton(
                direction = AisolverMoveDirection.Right,
                compact = compact,
                enabled = enabled,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MoveButton(
                direction = AisolverMoveDirection.Down,
                compact = compact,
                enabled = enabled,
                modifier = Modifier.weight(1f)
            )
            MoveButton(
                direction = AisolverMoveDirection.Left,
                compact = compact,
                enabled = enabled,
                modifier = Modifier.weight(1f)
            )
        }
        AisolverActionButton(
            text = "Analyze",
            onClick = {},
            enabled = enabled,
            compact = compact,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun MoveButton(
    direction: AisolverMoveDirection,
    compact: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    AisolverActionButton(
        text = "${direction.symbol} ${direction.label}",
        onClick = {},
        enabled = enabled,
        compact = compact,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun AisolverMoveControls_preview() {
    AisolverPreviewTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AisolverMoveControls(enabled = true, compact = false)
            AisolverMoveControls(enabled = false, compact = true)
        }
    }
}

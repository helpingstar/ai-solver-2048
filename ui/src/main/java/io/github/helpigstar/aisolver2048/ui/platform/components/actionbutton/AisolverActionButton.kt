package io.github.helpigstar.aisolver2048.ui.platform.components.actionbutton

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverPreviewTheme
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverUiTokens

@Composable
fun AisolverActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    compact: Boolean = false,
    containerColor: Color = AisolverUiTokens.ActionBrown,
) {
    Button(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = AisolverUiTokens.ButtonHeight),
        enabled = enabled,
        shape = RoundedCornerShape(AisolverUiTokens.ButtonRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = AisolverUiTokens.BrightText,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = AisolverUiTokens.BrightText.copy(alpha = 0.6f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = Dp.Hairline,
            pressedElevation = Dp.Hairline,
            disabledElevation = Dp.Hairline
        )
    ) {
        Text(
            text = text,
            style = if (compact) {
                MaterialTheme.typography.labelLarge
            } else {
                MaterialTheme.typography.bodyMedium
            }.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AisolverActionButton_preview() {
    AisolverPreviewTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AisolverActionButton(
                text = "Restart",
                onClick = {},
            )
            AisolverActionButton(
                text = "Retry",
                onClick = {},
                compact = true,
            )
            AisolverActionButton(
                text = "Disabled",
                onClick = {},
                enabled = false,
            )
            AisolverActionButton(
                text = "Best Move",
                onClick = {},
                containerColor = AisolverUiTokens.Gold,
            )
        }
    }
}

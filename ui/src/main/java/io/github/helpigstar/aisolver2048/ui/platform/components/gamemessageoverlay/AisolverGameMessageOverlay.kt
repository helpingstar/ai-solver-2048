package io.github.helpigstar.aisolver2048.ui.platform.components.gamemessageoverlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.helpigstar.aisolver2048.ui.platform.components.actionbutton.AisolverActionButton
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverPreviewTheme
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverUiTokens

enum class AisolverGameMessageType {
    Won,
    Over,
}

@Composable
fun AisolverGameMessageOverlay(
    type: AisolverGameMessageType,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    compact: Boolean = false,
    onRetry: () -> Unit = {},
    onKeepGoing: () -> Unit = {},
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = 800, delayMillis = 1200))
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    color = if (type == AisolverGameMessageType.Won) {
                        AisolverUiTokens.WinOverlay
                    } else {
                        AisolverUiTokens.DefaultOverlay
                    },
                    shape = RoundedCornerShape(AisolverUiTokens.BoardRadius)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(if (compact) 20.dp else 32.dp)
            ) {
                Text(
                    text = if (type == AisolverGameMessageType.Won) "You win!" else "Game over!",
                    color = if (type == AisolverGameMessageType.Won) {
                        AisolverUiTokens.BrightText
                    } else {
                        AisolverUiTokens.PrimaryText
                    },
                    style = MaterialTheme.typography.headlineLarge
                )
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    if (type == AisolverGameMessageType.Won) {
                        AisolverActionButton(
                            text = "Keep going",
                            onClick = onKeepGoing,
                            compact = compact
                        )
                    }
                    AisolverActionButton(
                        text = "Try again",
                        onClick = onRetry,
                        compact = compact
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun AisolverGameMessageOverlay_preview() {
    AisolverPreviewTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.size(280.dp)) {
                AisolverGameMessageOverlay(type = AisolverGameMessageType.Won, compact = true)
            }
            Box(modifier = Modifier.size(280.dp)) {
                AisolverGameMessageOverlay(type = AisolverGameMessageType.Over, compact = true)
            }
        }
    }
}

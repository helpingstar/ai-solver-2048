package io.github.helpigstar.aisolver2048.ui.platform.components.scoreadditionlabel

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverPreviewTheme
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverUiTokens
import kotlinx.coroutines.launch

@Composable
fun AisolverScoreAdditionLabel(
    delta: Int,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
) {
    val translationY = remember(delta) { Animatable(25f) }
    val alpha = remember(delta) { Animatable(0f) }

    LaunchedEffect(delta, visible) {
        if (visible) {
            translationY.snapTo(25f)
            alpha.snapTo(1f)
            launch {
                translationY.animateTo(
                    targetValue = -50f,
                    animationSpec = tween(durationMillis = 600)
                )
            }
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 600)
            )
        }
    }

    if (!visible && alpha.value <= 0f) {
        return
    }

    Text(
        text = "+$delta",
        modifier = modifier.graphicsLayer {
            this.alpha = alpha.value
            this.translationY = translationY.value
        },
        color = AisolverUiTokens.ScoreAddition,
        style = MaterialTheme.typography.titleLarge
    )
}

@Preview(showBackground = true)
@Composable
private fun AisolverScoreAdditionLabel_preview() {
    AisolverPreviewTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AisolverScoreAdditionLabel(delta = 32)
            AisolverScoreAdditionLabel(delta = 1024)
        }
    }
}

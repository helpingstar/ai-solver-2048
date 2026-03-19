package io.github.helpigstar.aisolver2048.ui.platform.components.numbertile

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverBoardMetrics
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverPreviewTheme
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverUiTokens
import io.github.helpigstar.aisolver2048.ui.platform.components.model.AisolverTileMotionState
import io.github.helpigstar.aisolver2048.ui.platform.components.model.AisolverTileUiModel

@Composable
fun AisolverNumberTile(
    tile: AisolverTileUiModel,
    metrics: AisolverBoardMetrics,
    modifier: Modifier = Modifier,
) {
    val appearance = AisolverUiTokens.tileAppearance(
        value = tile.value,
        isCompact = metrics.isCompact
    )
    val scale = remember(tile.row, tile.column, tile.value, tile.motionState) {
        Animatable(initialScaleFor(tile.motionState))
    }

    LaunchedEffect(tile.row, tile.column, tile.value, tile.motionState) {
        when (tile.motionState) {
            AisolverTileMotionState.Static -> scale.snapTo(1f)
            AisolverTileMotionState.New -> {
                scale.snapTo(0f)
                scale.animateTo(1f, animationSpec = tween(durationMillis = 180))
            }

            AisolverTileMotionState.Merged -> {
                scale.snapTo(0.85f)
                scale.animateTo(1.12f, animationSpec = tween(durationMillis = 120))
                scale.animateTo(1f, animationSpec = tween(durationMillis = 120))
            }
        }
    }

    Box(
        modifier = modifier
            .size(metrics.cellSize)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .drawGlow(appearance.glowColor)
            .background(
                color = appearance.background,
                shape = RoundedCornerShape(AisolverUiTokens.TileRadius)
            )
            .border(
                width = if (appearance.insetBorderColor == Color.Transparent) 0.dp else 1.dp,
                color = appearance.insetBorderColor,
                shape = RoundedCornerShape(AisolverUiTokens.TileRadius)
            )
            .then(
                if (tile.isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = AisolverUiTokens.Gold,
                        shape = RoundedCornerShape(AisolverUiTokens.TileRadius)
                    )
                } else {
                    Modifier
                }
            )
    ) {
        if (tile.isSelected) {
            SelectionTint()
        }

        Text(
            text = tile.value.toString(),
            modifier = Modifier.align(Alignment.Center),
            color = appearance.contentColor,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = appearance.fontSize
        )
    }
}

private fun Modifier.drawGlow(glowColor: Color): Modifier = drawBehind {
    if (glowColor == Color.Transparent) {
        return@drawBehind
    }

    val spread = 12.dp.toPx()
    drawRoundRect(
        color = glowColor,
        topLeft = androidx.compose.ui.geometry.Offset(-spread / 2, -spread / 2),
        size = androidx.compose.ui.geometry.Size(size.width + spread, size.height + spread),
        cornerRadius = CornerRadius(
            x = AisolverUiTokens.TileRadius.toPx() * 2,
            y = AisolverUiTokens.TileRadius.toPx() * 2
        )
    )
}

@Composable
private fun BoxScope.SelectionTint() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = AisolverUiTokens.SelectionTint,
                shape = RoundedCornerShape(AisolverUiTokens.TileRadius)
            )
    )
}

private fun initialScaleFor(motionState: AisolverTileMotionState): Float = when (motionState) {
    AisolverTileMotionState.Static -> 1f
    AisolverTileMotionState.New -> 0f
    AisolverTileMotionState.Merged -> 0.85f
}

@Preview(showBackground = true, widthDp = 420, heightDp = 360)
@Composable
private fun AisolverNumberTile_preview() {
    val metrics = AisolverUiTokens.boardMetrics(maxWidth = 500.dp)

    AisolverPreviewTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                listOf(
                    AisolverTileUiModel(0, 0, 2),
                    AisolverTileUiModel(0, 1, 64),
                    AisolverTileUiModel(0, 2, 128),
                ).forEach { tile ->
                    AisolverNumberTile(tile = tile, metrics = metrics)
                }
            }
            Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                listOf(
                    AisolverTileUiModel(0, 3, 1024),
                    AisolverTileUiModel(1, 0, 2048, isSelected = true),
                    AisolverTileUiModel(1, 1, 4096, motionState = AisolverTileMotionState.Merged)
                ).forEach { tile ->
                    AisolverNumberTile(tile = tile, metrics = metrics)
                }
            }
        }
    }
}

package io.github.helpigstar.aisolver2048.ui.platform.components.boardsurface

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverBoardMetrics
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverPreviewTheme
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverUiTokens

@Composable
fun AisolverBoardSurface(
    metrics: AisolverBoardMetrics,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .size(metrics.boardSize)
            .background(
                color = AisolverUiTokens.BoardBackground,
                shape = RoundedCornerShape(AisolverUiTokens.BoardRadius)
            )
            .padding(metrics.boardPadding),
        content = content
    )
}

@Preview(showBackground = true, widthDp = 420, heightDp = 760)
@Composable
private fun AisolverBoardSurface_preview() {
    AisolverPreviewTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val desktop = AisolverUiTokens.boardMetrics(500.dp)
            val compact = AisolverUiTokens.boardMetrics(280.dp)

            AisolverBoardSurface(metrics = desktop) {
                Box(
                    modifier = Modifier
                        .size(desktop.cellSize)
                        .background(AisolverUiTokens.BaseTile)
                )
            }
            AisolverBoardSurface(metrics = compact) {
                Box(
                    modifier = Modifier
                        .size(compact.cellSize)
                        .background(AisolverUiTokens.BaseTile)
                )
            }
        }
    }
}

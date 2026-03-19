package io.github.helpigstar.aisolver2048.ui.platform.components.bestmoveindicator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverPreviewTheme
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverUiTokens
import io.github.helpigstar.aisolver2048.ui.platform.components.model.AisolverMoveDirection

@Composable
fun AisolverBestMoveIndicator(
    bestMove: AisolverMoveDirection?,
    modifier: Modifier = Modifier,
) {
    if (bestMove == null) {
        return
    }

    Row(
        modifier = modifier
            .background(
                color = AisolverUiTokens.Gold.copy(alpha = 0.14f),
                shape = RoundedCornerShape(99.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Best move ${bestMove.symbol} ${bestMove.label}",
            color = AisolverUiTokens.PrimaryText
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AisolverBestMoveIndicator_preview() {
    AisolverPreviewTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AisolverBestMoveIndicator(bestMove = AisolverMoveDirection.Up)
            AisolverBestMoveIndicator(bestMove = AisolverMoveDirection.Left)
        }
    }
}

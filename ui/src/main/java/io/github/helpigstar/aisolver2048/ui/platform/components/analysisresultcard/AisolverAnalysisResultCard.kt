package io.github.helpigstar.aisolver2048.ui.platform.components.analysisresultcard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverPreviewTheme
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverUiTokens
import io.github.helpigstar.aisolver2048.ui.platform.components.model.AisolverMoveDirection
import io.github.helpigstar.aisolver2048.ui.platform.components.model.AisolverMoveRecommendationUiModel
import io.github.helpigstar.aisolver2048.ui.platform.components.model.AisolverWorkspaceSamples
import io.github.helpigstar.aisolver2048.ui.platform.components.moverankingrow.AisolverMoveRankingRow

@Composable
fun AisolverAnalysisResultCard(
    bestMove: AisolverMoveDirection?,
    recommendations: List<AisolverMoveRecommendationUiModel>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = AisolverUiTokens.BoardBackground,
                shape = RoundedCornerShape(AisolverUiTokens.BoardRadius)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row {
            Text(
                text = "AI Recommendation",
                color = AisolverUiTokens.BrightText,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            if (bestMove != null) {
                Text(
                    text = "${bestMove.symbol} ${bestMove.label}",
                    color = AisolverUiTokens.BaseTile,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        recommendations.forEach { recommendation ->
            AisolverMoveRankingRow(recommendation = recommendation)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AisolverAnalysisResultCard_preview() {
    AisolverPreviewTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AisolverAnalysisResultCard(
                bestMove = AisolverWorkspaceSamples.default().bestMove,
                recommendations = AisolverWorkspaceSamples.default().recommendations
            )
            AisolverAnalysisResultCard(
                bestMove = null,
                recommendations = AisolverWorkspaceSamples.selected().recommendations
            )
        }
    }
}

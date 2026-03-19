package io.github.helpigstar.aisolver2048.ui.platform.components.moverankingrow

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverPreviewTheme
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverUiTokens
import io.github.helpigstar.aisolver2048.ui.platform.components.model.AisolverMoveDirection
import io.github.helpigstar.aisolver2048.ui.platform.components.model.AisolverMoveRecommendationUiModel

@Composable
fun AisolverMoveRankingRow(
    recommendation: AisolverMoveRecommendationUiModel,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val rowModifier = modifier
        .fillMaxWidth()
        .background(
            color = if (recommendation.isBest) {
                AisolverUiTokens.Gold.copy(alpha = 0.28f)
            } else {
                AisolverUiTokens.BaseTile
            },
            shape = RoundedCornerShape(AisolverUiTokens.TileRadius)
        )
        .alpha(if (recommendation.isEnabled) 1f else 0.55f)
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    enabled = recommendation.isEnabled,
                    onClick = onClick
                )
            } else {
                Modifier
            }
        )

    Row(
        modifier = rowModifier.padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${recommendation.direction.symbol} ${recommendation.direction.label}",
            color = AisolverUiTokens.PrimaryText,
            fontWeight = if (recommendation.isBest) FontWeight.Bold else FontWeight.Medium
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(text = recommendation.probabilityText, color = AisolverUiTokens.PrimaryText)
        Text(
            text = recommendation.expectedScoreText,
            color = AisolverUiTokens.PrimaryText,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AisolverMoveRankingRow_preview() {
    AisolverPreviewTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AisolverMoveRankingRow(
                recommendation = AisolverMoveRecommendationUiModel(
                    direction = AisolverMoveDirection.Up,
                    probabilityText = "34%",
                    expectedScoreText = "4096",
                    isBest = true,
                    isEnabled = true
                )
            )
            AisolverMoveRankingRow(
                recommendation = AisolverMoveRecommendationUiModel(
                    direction = AisolverMoveDirection.Right,
                    probabilityText = "28%",
                    expectedScoreText = "3072",
                    isEnabled = true
                )
            )
            AisolverMoveRankingRow(
                recommendation = AisolverMoveRecommendationUiModel(
                    direction = AisolverMoveDirection.Left,
                    probabilityText = "17%",
                    expectedScoreText = "1536",
                    isEnabled = false
                )
            )
        }
    }
}

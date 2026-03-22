package io.github.helpigstar.aisolver2048.ui.platform.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.helpigstar.aisolver2048.ui.platform.theme.color.defaultAisolverColorScheme

data class AisolverRecommendation(
    val direction: AisolverRecommendationDirection,
    val confidencePercent: Int,
)

object AisolverRecommendationListDefaults {
    val ContainerColor = defaultAisolverColorScheme.background.primary
}

@Composable
fun AisolverRecommendationList(
    recommendations: List<AisolverRecommendation>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AisolverRecommendationListDefaults.ContainerColor),
    ) {
        recommendations.forEachIndexed { index, recommendation ->
            AisolverRecommendationItem(
                direction = recommendation.direction,
                confidencePercent = recommendation.confidencePercent,
                showDivider = index < recommendations.lastIndex,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AisolverRecommendationListPreview() {
    MaterialTheme {
        AisolverRecommendationList(
            recommendations = listOf(
                AisolverRecommendation(
                    direction = AisolverRecommendationDirection.Up,
                    confidencePercent = 78,
                ),
                AisolverRecommendation(
                    direction = AisolverRecommendationDirection.Right,
                    confidencePercent = 15,
                ),
                AisolverRecommendation(
                    direction = AisolverRecommendationDirection.Left,
                    confidencePercent = 15,
                ),
                AisolverRecommendation(
                    direction = AisolverRecommendationDirection.Down,
                    confidencePercent = 15,
                ),
            ),
            modifier = Modifier.width(353.dp),
        )
    }
}

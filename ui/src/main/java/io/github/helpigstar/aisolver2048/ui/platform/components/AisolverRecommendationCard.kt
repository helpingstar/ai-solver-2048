package io.github.helpigstar.aisolver2048.ui.platform.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.helpigstar.aisolver2048.ui.platform.theme.color.defaultAisolverColorScheme

object AisolverRecommendationCardDefaults {
    val Shape = RoundedCornerShape(16.dp)
    val BorderThickness = 1.dp

    val ContainerColor = defaultAisolverColorScheme.background.primary
    val BorderColor = defaultAisolverColorScheme.stroke.border
}

@Composable
fun AisolverRecommendationCard(
    recommendations: List<AisolverRecommendation>,
    onAnalyzeClick: () -> Unit,
    onRecommendationClick: ((AisolverRecommendationDirection) -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    animateRecommendationChanges: Boolean = true,
    title: String = "AI Recommendation",
    analyzeButtonLabel: String = "Analyze",
    analyzeButtonContentDescription: String? = null,
) {
    Surface(
        modifier = modifier,
        shape = AisolverRecommendationCardDefaults.Shape,
        color = AisolverRecommendationCardDefaults.ContainerColor,
        border = BorderStroke(
            width = AisolverRecommendationCardDefaults.BorderThickness,
            color = AisolverRecommendationCardDefaults.BorderColor,
        ),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            AisolverRecommendationHeader(
                onAnalyzeClick = onAnalyzeClick,
                enabled = enabled,
                title = title,
                analyzeButtonLabel = analyzeButtonLabel,
                analyzeButtonContentDescription = analyzeButtonContentDescription,
            )
            AisolverRecommendationList(
                recommendations = recommendations,
                animateRecommendationChanges = animateRecommendationChanges,
                onRecommendationClick = onRecommendationClick,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AisolverRecommendationCardPreview() {
    MaterialTheme {
        AisolverRecommendationCard(
            recommendations = listOf(
                AisolverRecommendation(
                    direction = AisolverRecommendationDirection.Up,
                    confidencePercent = 78f,
                ),
                AisolverRecommendation(
                    direction = AisolverRecommendationDirection.Right,
                    confidencePercent = 15f,
                ),
                AisolverRecommendation(
                    direction = AisolverRecommendationDirection.Left,
                    confidencePercent = 15f,
                ),
                AisolverRecommendation(
                    direction = AisolverRecommendationDirection.Down,
                    confidencePercent = 15f,
                ),
            ),
            onAnalyzeClick = {},
            modifier = Modifier.width(355.dp),
        )
    }
}

package io.github.helpigstar.aisolver2048.ui.platform.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import io.github.helpigstar.aisolver2048.ui.platform.theme.color.defaultAisolverColorScheme

data class AisolverRecommendation(
    val direction: AisolverRecommendationDirection,
    val confidencePercent: Float,
)

object AisolverRecommendationListDefaults {
    val ContainerColor = defaultAisolverColorScheme.background.primary
    const val PlacementAnimationDurationMillis = 300
}

@Composable
fun AisolverRecommendationList(
    recommendations: List<AisolverRecommendation>,
    modifier: Modifier = Modifier,
    animateRecommendationChanges: Boolean = true,
    onRecommendationClick: ((AisolverRecommendationDirection) -> Unit)? = null,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .height(recommendations.listHeight())
            .background(AisolverRecommendationListDefaults.ContainerColor),
        userScrollEnabled = false,
    ) {
        items(
            items = recommendations,
            key = { recommendation -> recommendation.direction },
        ) { recommendation ->
            AisolverRecommendationItem(
                direction = recommendation.direction,
                confidencePercent = recommendation.confidencePercent,
                animateValueChanges = animateRecommendationChanges,
                onClick = onRecommendationClick?.let { onClick ->
                    { onClick(recommendation.direction) }
                },
                modifier = Modifier.animateItem(
                    fadeInSpec = null,
                    placementSpec = tween(
                        durationMillis = AisolverRecommendationListDefaults.PlacementAnimationDurationMillis,
                    ),
                    fadeOutSpec = null,
                ),
                showDivider = recommendation != recommendations.last(),
            )
        }
    }
}

private fun List<AisolverRecommendation>.listHeight() =
    (AisolverRecommendationItemDefaults.ItemHeight * size) +
        (AisolverRecommendationItemDefaults.DividerThickness * (size - 1).coerceAtLeast(0))

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AisolverRecommendationListPreview() {
    MaterialTheme {
        AisolverRecommendationList(
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
            modifier = Modifier.width(353.dp),
        )
    }
}

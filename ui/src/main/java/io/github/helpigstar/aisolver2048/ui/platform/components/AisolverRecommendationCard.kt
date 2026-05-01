package io.github.helpigstar.aisolver2048.ui.platform.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.helpigstar.aisolver2048.core.model.MoveDirection
import io.github.helpigstar.aisolver2048.ui.platform.components.button.color.aisolverAutoFilledButtonColors
import io.github.helpigstar.aisolver2048.ui.platform.components.util.rememberVectorPainter
import io.github.helpigstar.aisolver2048.ui.platform.resource.AisolverDrawable
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
    onAutoMoveClick: () -> Unit = {},
    onRecommendationClick: ((MoveDirection) -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    autoButtonEnabled: Boolean = true,
    animateRecommendationChanges: Boolean = true,
    title: String = "Best move",
    analyzeButtonLabel: String = "Analyze",
    analyzeButtonContentDescription: String? = null,
    autoButtonLabel: String = "Auto",
    autoButtonIcon: Painter = rememberVectorPainter(id = AisolverDrawable.ic_play_circle),
    autoButtonColors: ButtonColors = aisolverAutoFilledButtonColors(),
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
                onAutoMoveClick = onAutoMoveClick,
                enabled = enabled,
                autoButtonEnabled = autoButtonEnabled,
                title = title,
                analyzeButtonLabel = analyzeButtonLabel,
                analyzeButtonContentDescription = analyzeButtonContentDescription,
                autoButtonLabel = autoButtonLabel,
                autoButtonIcon = autoButtonIcon,
                autoButtonColors = autoButtonColors,
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
                    direction = MoveDirection.Left,
                    confidencePercent = 78f,
                ),
                AisolverRecommendation(
                    direction = MoveDirection.Right,
                    confidencePercent = 15f,
                ),
                AisolverRecommendation(
                    direction = MoveDirection.Up,
                    confidencePercent = 15f,
                ),
                AisolverRecommendation(
                    direction = MoveDirection.Down,
                    confidencePercent = 15f,
                ),
            ),
            onAnalyzeClick = {},
            onAutoMoveClick = {},
            modifier = Modifier.width(355.dp),
        )
    }
}

package io.github.helpigstar.aisolver2048.ui.platform.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.helpigstar.aisolver2048.ui.platform.components.button.AisolverAnalyzeButton
import io.github.helpigstar.aisolver2048.ui.platform.components.button.AisolverAutoAnalyzeButton
import io.github.helpigstar.aisolver2048.ui.platform.components.button.AisolverAutoAnalyzeButtonVariant
import io.github.helpigstar.aisolver2048.ui.platform.theme.color.defaultAisolverColorScheme

object AisolverRecommendationHeaderDefaults {
    val HorizontalPadding = 16.dp
    val VerticalPadding = 8.dp
    val ContentSpacing = 12.dp
    val DividerThickness = 1.dp

    val TitleColor = defaultAisolverColorScheme.text.primary
    val DividerColor = defaultAisolverColorScheme.stroke.divider
}

@Composable
fun AisolverRecommendationHeader(
    onAnalyzeClick: () -> Unit,
    onAutoMoveClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    autoButtonEnabled: Boolean = true,
    autoButtonVariant: AisolverAutoAnalyzeButtonVariant = AisolverAutoAnalyzeButtonVariant.Auto,
    title: String = "Best move",
    analyzeButtonLabel: String = "Analyze",
    analyzeButtonContentDescription: String? = null,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = AisolverRecommendationHeaderDefaults.HorizontalPadding,
                    vertical = AisolverRecommendationHeaderDefaults.VerticalPadding,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                color = AisolverRecommendationHeaderDefaults.TitleColor,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 32.sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(AisolverRecommendationHeaderDefaults.ContentSpacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AisolverAnalyzeButton(
                    onClick = onAnalyzeClick,
                    enabled = enabled,
                    label = analyzeButtonLabel,
                    contentDescription = analyzeButtonContentDescription,
                )
                AisolverAutoAnalyzeButton(
                    variant = autoButtonVariant,
                    onClick = onAutoMoveClick,
                    enabled = autoButtonEnabled,
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.align(Alignment.BottomCenter),
            thickness = AisolverRecommendationHeaderDefaults.DividerThickness,
            color = AisolverRecommendationHeaderDefaults.DividerColor,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AisolverRecommendationHeaderPreview() {
    MaterialTheme {
        AisolverRecommendationHeader(
            onAnalyzeClick = {},
            onAutoMoveClick = {},
            modifier = Modifier.width(354.dp),
        )
    }
}

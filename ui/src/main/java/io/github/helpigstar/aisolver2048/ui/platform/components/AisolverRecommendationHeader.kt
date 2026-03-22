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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.helpigstar.aisolver2048.ui.platform.components.button.AisolverAnalyzeButton
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
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    title: String = "AI Recommendation",
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
            horizontalArrangement = Arrangement.spacedBy(AisolverRecommendationHeaderDefaults.ContentSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                color = AisolverRecommendationHeaderDefaults.TitleColor,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                ),
            )
            AisolverAnalyzeButton(
                onClick = onAnalyzeClick,
                modifier = Modifier.weight(1f),
                enabled = enabled,
                label = analyzeButtonLabel,
                contentDescription = analyzeButtonContentDescription,
            )
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
            modifier = Modifier.width(354.dp),
        )
    }
}

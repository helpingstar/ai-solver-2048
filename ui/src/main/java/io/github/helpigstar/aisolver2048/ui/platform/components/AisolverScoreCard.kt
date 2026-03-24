package io.github.helpigstar.aisolver2048.ui.platform.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.helpigstar.aisolver2048.ui.platform.components.button.AisolverActionButtonDefaults
import io.github.helpigstar.aisolver2048.ui.platform.theme.color.defaultAisolverColorScheme

object AisolverScoreCardDefaults {
    val MinWidth = 164.dp
    val Height = 52.dp
    val HorizontalPadding = 16.dp
    val ContentSpacing = 8.dp
    val Shape = RoundedCornerShape(14.dp)

    val ContainerColor = defaultAisolverColorScheme.background.tertiary
    val LabelColor = defaultAisolverColorScheme.text.tertiary
    val ValueColor = defaultAisolverColorScheme.text.primary
}

@Composable
fun AisolverScoreCard(
    score: Int,
    modifier: Modifier = Modifier,
    label: String = "Score",
) {
    Row(
        modifier = modifier
            .defaultMinSize(
                minWidth = AisolverScoreCardDefaults.MinWidth,
                minHeight = AisolverScoreCardDefaults.Height,
            )
            .clip(AisolverScoreCardDefaults.Shape)
            .background(AisolverScoreCardDefaults.ContainerColor)
            .padding(horizontal = AisolverScoreCardDefaults.HorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(AisolverScoreCardDefaults.ContentSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = AisolverScoreCardDefaults.LabelColor,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            ),
        )
        Text(
            text = score.toString(),
            color = AisolverScoreCardDefaults.ValueColor,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = 32.sp,
            ),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AisolverScoreCardPreview() {
    MaterialTheme {
        AisolverScoreCard(
            score = 1024,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AisolverScoreCardRowPreview() {
    MaterialTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AisolverScoreCard(
                score = 1024,
                modifier = Modifier.width(164.dp),
            )
            AisolverScoreCard(
                score = 4096,
                label = "Best",
                modifier = Modifier.width(164.dp),
            )
        }
    }
}

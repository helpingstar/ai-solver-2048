package io.github.helpigstar.aisolver2048.ui.platform.components.scorebadge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverPreviewTheme
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverUiTokens
import io.github.helpigstar.aisolver2048.ui.platform.components.scoreadditionlabel.AisolverScoreAdditionLabel

@Composable
fun AisolverScoreBadge(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    scoreDelta: Int? = null,
) {
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 94.dp)
            .height(AisolverUiTokens.BadgeHeight)
            .clip(RoundedCornerShape(AisolverUiTokens.TileRadius))
            .background(AisolverUiTokens.BoardBackground)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label.uppercase(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(),
            color = AisolverUiTokens.BaseTile,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = value,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = AisolverUiTokens.BrightText,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        if (scoreDelta != null) {
            AisolverScoreAdditionLabel(
                delta = scoreDelta,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AisolverScoreBadge_preview() {
    AisolverPreviewTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AisolverScoreBadge(
                label = "Score",
                value = "32480",
                scoreDelta = 128
            )
            AisolverScoreBadge(
                label = "Best",
                value = "4096"
            )
        }
    }
}

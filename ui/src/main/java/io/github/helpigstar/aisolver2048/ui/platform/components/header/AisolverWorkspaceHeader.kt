package io.github.helpigstar.aisolver2048.ui.platform.components.header

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.helpigstar.aisolver2048.ui.platform.components.actionbutton.AisolverActionButton
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverPreviewTheme
import io.github.helpigstar.aisolver2048.ui.platform.components.scorebadge.AisolverScoreBadge

@Composable
fun AisolverWorkspaceHeader(
    score: Int,
    bestTile: Int,
    scoreDelta: Int?,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val compact = maxWidth < 420.dp

        if (compact) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                HeaderBadges(score = score, bestTile = bestTile, scoreDelta = scoreDelta)
                HeaderActions(compact = true)
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderBadges(
                    score = score,
                    bestTile = bestTile,
                    scoreDelta = scoreDelta,
                    modifier = Modifier.weight(1f)
                )
                HeaderActions(compact = false)
            }
        }
    }
}

@Composable
private fun HeaderBadges(
    score: Int,
    bestTile: Int,
    scoreDelta: Int?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AisolverScoreBadge(
            label = "Score",
            value = score.toString(),
            scoreDelta = scoreDelta,
            modifier = Modifier.weight(1f)
        )
        AisolverScoreBadge(
            label = "Best",
            value = bestTile.toString(),
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true, widthDp = 560, heightDp = 320)
@Composable
private fun AisolverWorkspaceHeader_preview() {
    AisolverPreviewTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                AisolverWorkspaceHeader(
                    score = 32480,
                    bestTile = 4096,
                    scoreDelta = 128,
                )
            }
            Box(modifier = Modifier.fillMaxWidth()) {
                AisolverWorkspaceHeader(
                    score = 128,
                    bestTile = 256,
                    scoreDelta = null,
                )
            }
        }
    }
}

@Composable
private fun HeaderActions(
    compact: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
    ) {
        AisolverActionButton(
            text = "Undo",
            onClick = {},
            compact = compact
        )
        AisolverActionButton(
            text = "Reset",
            onClick = {},
            compact = compact
        )
    }
}

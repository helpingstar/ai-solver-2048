package io.github.helpigstar.aisolver2048.ui.platform.components.gridcell

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverPreviewTheme
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverUiTokens

@Composable
fun AisolverGridCell(
    size: Dp,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
) {
    Box(
        modifier = modifier
            .size(size)
            .background(
                color = AisolverUiTokens.GridCellFill,
                shape = RoundedCornerShape(AisolverUiTokens.TileRadius)
            )
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = AisolverUiTokens.Gold,
                        shape = RoundedCornerShape(AisolverUiTokens.TileRadius)
                    )
                } else {
                    Modifier
                }
            )
    )
}

@Preview(showBackground = true)
@Composable
private fun AisolverGridCell_preview() {
    AisolverPreviewTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AisolverGridCell(size = 64.dp)
            AisolverGridCell(size = 64.dp, isSelected = true)
        }
    }
}

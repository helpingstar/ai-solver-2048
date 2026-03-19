package io.github.helpigstar.aisolver2048.ui.platform.components.boardcell

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverPreviewTheme
import io.github.helpigstar.aisolver2048.ui.platform.components.gridcell.AisolverGridCell

@Composable
fun AisolverBoardCell(
    size: Dp,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
) {
    AisolverGridCell(
        size = size,
        modifier = modifier,
        isSelected = isSelected
    )
}

@Preview(showBackground = true)
@Composable
private fun AisolverBoardCell_preview() {
    AisolverPreviewTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AisolverBoardCell(size = 64.dp)
            AisolverBoardCell(size = 64.dp, isSelected = true)
        }
    }
}

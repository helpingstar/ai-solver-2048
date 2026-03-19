package io.github.helpigstar.aisolver2048.ui.platform.components.board

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.helpigstar.aisolver2048.ui.platform.components.boardcell.AisolverBoardCell
import io.github.helpigstar.aisolver2048.ui.platform.components.boardsurface.AisolverBoardSurface
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverBoardMetrics
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverPreviewTheme
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverUiTokens
import io.github.helpigstar.aisolver2048.ui.platform.components.model.AisolverBoardPosition
import io.github.helpigstar.aisolver2048.ui.platform.components.model.AisolverMoveDirection
import io.github.helpigstar.aisolver2048.ui.platform.components.model.AisolverTileUiModel
import io.github.helpigstar.aisolver2048.ui.platform.components.model.AisolverWorkspaceSamples
import io.github.helpigstar.aisolver2048.ui.platform.components.numbertile.AisolverNumberTile

@Composable
fun AisolverBoard(
    tiles: List<AisolverTileUiModel>,
    selectedCell: AisolverBoardPosition?,
    bestMove: AisolverMoveDirection?,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val metrics = AisolverUiTokens.boardMetrics(maxWidth = maxWidth)

        AisolverBoardSurface(metrics = metrics) {
            BoardGrid(
                metrics = metrics,
                selectedCell = selectedCell
            )
            tiles.forEach { tile ->
                AisolverNumberTile(
                    tile = tile,
                    metrics = metrics,
                    modifier = Modifier.offset(
                        x = metrics.offsetFor(tile.column),
                        y = metrics.offsetFor(tile.row)
                    )
                )
            }

            if (bestMove != null) {
                BestMoveChip(
                    direction = bestMove,
                    compact = metrics.isCompact,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }
    }
}

@Composable
private fun BoardGrid(
    metrics: AisolverBoardMetrics,
    selectedCell: AisolverBoardPosition?,
) {
    Column(verticalArrangement = Arrangement.spacedBy(metrics.gap)) {
        repeat(4) { rowIndex ->
            Row(horizontalArrangement = Arrangement.spacedBy(metrics.gap)) {
                repeat(4) { columnIndex ->
                    AisolverBoardCell(
                        size = metrics.cellSize,
                        isSelected = selectedCell?.row == rowIndex &&
                            selectedCell.column == columnIndex
                    )
                }
            }
        }
    }
}

@Composable
private fun BestMoveChip(
    direction: AisolverMoveDirection,
    compact: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(
                color = AisolverUiTokens.Gold.copy(alpha = 0.18f),
                shape = RoundedCornerShape(99.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = "BEST ${direction.symbol}",
            color = AisolverUiTokens.PrimaryText,
            style = if (compact) {
                MaterialTheme.typography.labelMedium
            } else {
                MaterialTheme.typography.labelLarge
            }.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Preview(showBackground = true, widthDp = 420, heightDp = 760)
@Composable
private fun AisolverBoard_preview() {
    AisolverPreviewTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AisolverBoard(
                tiles = AisolverWorkspaceSamples.default().tiles,
                selectedCell = null,
                bestMove = AisolverWorkspaceSamples.default().bestMove,
                modifier = Modifier.size(320.dp)
            )
            AisolverBoard(
                tiles = AisolverWorkspaceSamples.selected().tiles,
                selectedCell = AisolverWorkspaceSamples.selected().selectedCell,
                bestMove = AisolverWorkspaceSamples.selected().bestMove,
                modifier = Modifier.size(320.dp)
            )
        }
    }
}

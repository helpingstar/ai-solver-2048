package io.github.helpigstar.aisolver2048.ui.platform.components.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.helpigstar.aisolver2048.ui.platform.components.analysisresultcard.AisolverAnalysisResultCard
import io.github.helpigstar.aisolver2048.ui.platform.components.bestmoveindicator.AisolverBestMoveIndicator
import io.github.helpigstar.aisolver2048.ui.platform.components.board.AisolverBoard
import io.github.helpigstar.aisolver2048.ui.platform.components.editcontrols.AisolverEditControls
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverPreviewTheme
import io.github.helpigstar.aisolver2048.ui.platform.components.foundation.AisolverUiTokens
import io.github.helpigstar.aisolver2048.ui.platform.components.header.AisolverWorkspaceHeader
import io.github.helpigstar.aisolver2048.ui.platform.components.model.AisolverControlMode
import io.github.helpigstar.aisolver2048.ui.platform.components.model.AisolverWorkspaceSamples
import io.github.helpigstar.aisolver2048.ui.platform.components.model.AisolverWorkspaceUiModel
import io.github.helpigstar.aisolver2048.ui.platform.components.movecontrols.AisolverMoveControls
import io.github.helpigstar.aisolver2048.ui.platform.components.valuepickerbottomsheet.AisolverValuePickerBottomSheet

@Composable
fun AisolverWorkspaceScreen(
    modifier: Modifier = Modifier,
    model: AisolverWorkspaceUiModel = AisolverWorkspaceSamples.default(),
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(AisolverUiTokens.PageBackground)
    ) {
        val compact = maxWidth < AisolverUiTokens.Breakpoint

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = AisolverUiTokens.DesktopBoardSize),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AisolverWorkspaceHeader(
                    score = model.score,
                    bestTile = model.bestTile,
                    scoreDelta = model.scoreDelta
                )
                AisolverBoard(
                    tiles = model.tiles,
                    selectedCell = model.selectedCell,
                    bestMove = model.bestMove
                )
                AisolverBestMoveIndicator(bestMove = model.bestMove)
                AisolverAnalysisResultCard(
                    bestMove = model.bestMove,
                    recommendations = model.recommendations
                )
                when (model.controlMode) {
                    AisolverControlMode.Move -> {
                        AisolverMoveControls(
                            enabled = model.selectedCell == null,
                            compact = compact
                        )
                    }

                    AisolverControlMode.Edit -> {
                        AisolverEditControls(compact = compact)
                    }
                }
                if (model.showValuePicker) {
                    AisolverValuePickerBottomSheet(showExtendedValues = true)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 420, heightDp = 1600)
@Composable
private fun AisolverWorkspaceScreen_preview() {
    AisolverPreviewTheme {
        Column(verticalArrangement = Arrangement.spacedBy(100.dp)) {
            AisolverWorkspaceScreen(model = AisolverWorkspaceSamples.default())
            AisolverWorkspaceScreen(model = AisolverWorkspaceSamples.selected())
            AisolverWorkspaceScreen(model = AisolverWorkspaceSamples.valuePicker())
        }
    }
}

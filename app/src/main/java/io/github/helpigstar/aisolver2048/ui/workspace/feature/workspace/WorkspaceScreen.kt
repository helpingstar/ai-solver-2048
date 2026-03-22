package io.github.helpigstar.aisolver2048.ui.workspace.feature.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBoard
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBoardDefaults
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBoardPosition
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBoardTile
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverGameActions
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverRecommendation
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverRecommendationCard
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverScoreCard
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverScoreCardDefaults
import io.github.helpigstar.aisolver2048.ui.platform.theme.color.defaultAisolverColorScheme
import io.github.helpigstar.aisolver2048.ui.theme.AiSolver2048Theme

private const val BOARD_COLUMN_COUNT: Int = 4
private const val EMPTY_CELL_VALUE: Int = 0

@Composable
fun WorkspaceScreen(
    viewModel: WorkspaceViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    WorkspaceScreen(
        state = state,
        onAction = viewModel::trySendAction,
    )
}

@Composable
private fun WorkspaceScreen(
    state: WorkspaceState,
    onAction: (WorkspaceAction) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = defaultAisolverColorScheme.background.primary,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(top = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            WorkspaceStatusSection(
                score = state.score,
                canUndo = state.canUndo,
                canReset = state.canReset,
                onUndoClick = { onAction(WorkspaceAction.UndoClick) },
                onResetClick = { onAction(WorkspaceAction.ResetClick) },
            )
            AisolverBoard(
                tiles = state.boardValues.toBoardTiles(),
                selectedPosition = state.selectedCellIndex?.toBoardPosition(),
                onCellClick = { position ->
                    onAction(WorkspaceAction.CellClick(position.toCellIndex()))
                },
            )
            AisolverRecommendationCard(
                recommendations = state.recommendations.toRecommendationModels(),
                onAnalyzeClick = { onAction(WorkspaceAction.AnalyzeClick) },
                modifier = Modifier.width(AisolverBoardDefaults.BoardSize),
                enabled = state.canAnalyze,
            )
            if (state.selectedCellIndex != null) {
                WorkspaceEditControls(
                    onClearClick = { onAction(WorkspaceAction.ClearSelectedCellClick) },
                    onSetTwoClick = { onAction(WorkspaceAction.SetSelectedCellValueToTwoClick) },
                    onSetFourClick = { onAction(WorkspaceAction.SetSelectedCellValueToFourClick) },
                    modifier = Modifier.width(AisolverBoardDefaults.BoardSize),
                )
            }
        }
    }
}

@Composable
private fun WorkspaceStatusSection(
    score: Int,
    canUndo: Boolean,
    canReset: Boolean,
    onUndoClick: () -> Unit,
    onResetClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(defaultAisolverColorScheme.background.primary),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AisolverScoreCard(
                score = score,
                modifier = Modifier.width(AisolverScoreCardDefaults.MinWidth),
            )
            AisolverGameActions(
                onUndoClick = onUndoClick,
                onResetClick = onResetClick,
                undoEnabled = canUndo,
                resetEnabled = canReset,
            )
        }
    }
}

@Composable
private fun WorkspaceEditControls(
    onClearClick: () -> Unit,
    onSetTwoClick: () -> Unit,
    onSetFourClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        WorkspaceEditButton(
            label = "Clear",
            onClick = onClearClick,
            modifier = Modifier.weight(1f),
        )
        WorkspaceEditButton(
            label = "2",
            onClick = onSetTwoClick,
            modifier = Modifier.weight(1f),
        )
        WorkspaceEditButton(
            label = "4",
            onClick = onSetFourClick,
            modifier = Modifier.weight(1f),
        )
        WorkspaceEditButton(
            label = "x2",
            onClick = {},
            enabled = false,
            modifier = Modifier.weight(1f),
        )
        WorkspaceEditButton(
            label = "÷2",
            onClick = {},
            enabled = false,
            modifier = Modifier.weight(1f),
        )
        WorkspaceEditButton(
            label = "More",
            onClick = {},
            enabled = false,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun WorkspaceEditButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(40.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = defaultAisolverColorScheme.button.utilityBackground,
            contentColor = defaultAisolverColorScheme.button.utilityForeground,
            disabledContainerColor = defaultAisolverColorScheme.button.utilityBackgroundDisabled,
            disabledContentColor = defaultAisolverColorScheme.button.utilityForegroundDisabled,
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
            disabledElevation = 0.dp,
        ),
        contentPadding = PaddingValues(horizontal = 0.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            maxLines = 1,
        )
    }
}

private fun List<Int>.toBoardTiles(): List<AisolverBoardTile> =
    mapIndexedNotNull { index, value ->
        if (value == EMPTY_CELL_VALUE) {
            null
        } else {
            AisolverBoardTile(
                id = "tile-$index",
                value = value,
                position = index.toBoardPosition(),
            )
        }
    }

private fun Int.toBoardPosition(): AisolverBoardPosition =
    AisolverBoardPosition(
        row = this / BOARD_COLUMN_COUNT,
        column = this % BOARD_COLUMN_COUNT,
    )

private fun AisolverBoardPosition.toCellIndex(): Int = (row * BOARD_COLUMN_COUNT) + column

private fun List<WorkspaceRecommendationUi>.toRecommendationModels(): List<AisolverRecommendation> =
    map { recommendation ->
        AisolverRecommendation(
            direction = recommendation.direction,
            confidencePercent = recommendation.confidencePercent,
        )
    }

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun WorkspaceScreenPreview() {
    AiSolver2048Theme {
        WorkspaceScreen(
            state = WorkspaceState(
                boardValues = listOf(
                    2, 0, 4, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0,
                ),
                score = 0,
                selectedCellIndex = 0,
                canUndo = true,
                canReset = true,
                canAnalyze = true,
                recommendations = listOf(
                    WorkspaceRecommendationUi(
                        direction = io.github.helpigstar.aisolver2048.ui.platform.components.AisolverRecommendationDirection.Up,
                        confidencePercent = 0f,
                    ),
                    WorkspaceRecommendationUi(
                        direction = io.github.helpigstar.aisolver2048.ui.platform.components.AisolverRecommendationDirection.Right,
                        confidencePercent = 0f,
                    ),
                    WorkspaceRecommendationUi(
                        direction = io.github.helpigstar.aisolver2048.ui.platform.components.AisolverRecommendationDirection.Left,
                        confidencePercent = 0f,
                    ),
                    WorkspaceRecommendationUi(
                        direction = io.github.helpigstar.aisolver2048.ui.platform.components.AisolverRecommendationDirection.Down,
                        confidencePercent = 0f,
                    ),
                ),
            ),
            onAction = {},
        )
    }
}

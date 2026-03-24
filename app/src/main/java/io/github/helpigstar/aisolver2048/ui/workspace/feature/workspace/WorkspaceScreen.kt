package io.github.helpigstar.aisolver2048.ui.workspace.feature.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.helpigstar.aisolver2048.data.workspace.manager.WorkspaceRecommendationDirection
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBoard
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBoardDefaults
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBoardPosition
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBoardSwipeDirection
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBoardTile
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBottomSheet
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBottomSheetItem
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverGameActions
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverRecommendation
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverRecommendationCard
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverRecommendationDirection
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverScoreCard
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverSettingsDialog
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverSettingsItemModel
import io.github.helpigstar.aisolver2048.ui.platform.theme.color.defaultAisolverColorScheme
import io.github.helpigstar.aisolver2048.ui.theme.AiSolver2048Theme

private const val BOARD_COLUMN_COUNT: Int = 4

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
@OptIn(ExperimentalMaterial3Api::class)
private fun WorkspaceScreen(
    state: WorkspaceState,
    onAction: (WorkspaceAction) -> Unit,
) {
    val canEditBoardCells = !state.isInteractionLocked && !state.isEditBottomSheetVisible
    val canTriggerMove = !state.isInteractionLocked && !state.isEditBottomSheetVisible && state.canAnalyze
    val canOpenSettings = !state.isInteractionLocked && !state.isEditBottomSheetVisible

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
                .padding(top = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            WorkspaceStatusSection(
                score = state.score,
                canUndo = state.canUndo && !state.isInteractionLocked && !state.isEditBottomSheetVisible,
                canReset = state.canReset && !state.isInteractionLocked && !state.isEditBottomSheetVisible,
                canOpenSettings = canOpenSettings,
                onUndoClick = { onAction(WorkspaceAction.UndoClick) },
                onResetClick = { onAction(WorkspaceAction.ResetClick) },
                onSettingsClick = { onAction(WorkspaceAction.SettingsClick) },
            )
            AisolverBoard(
                tiles = state.boardTiles.toBoardTiles(),
                onCellClick = if (canEditBoardCells) { position ->
                    onAction(WorkspaceAction.CellClick(position.toCellIndex()))
                } else {
                    null
                },
                onSwipe = if (canTriggerMove) { direction ->
                    onAction(WorkspaceAction.Move(direction.toWorkspaceDirection()))
                } else {
                    null
                },
            )
            AisolverRecommendationCard(
                recommendations = state.recommendations.toRecommendationModels(),
                onAnalyzeClick = { onAction(WorkspaceAction.AnalyzeClick) },
                onRecommendationClick = if (canTriggerMove) { direction ->
                    onAction(WorkspaceAction.Move(direction.toWorkspaceDirection()))
                } else {
                    null
                },
                modifier = Modifier.width(AisolverBoardDefaults.BoardSize),
                enabled = state.canAnalyze &&
                    state.isAnalyzeAvailable &&
                    !state.isInteractionLocked &&
                    !state.isEditBottomSheetVisible,
                analyzeButtonLabel = if (state.isAnalyzing) "Analyzing" else "Analyze",
                animateRecommendationChanges = state.animateRecommendationChanges,
            )
        }

        if (state.isEditBottomSheetVisible) {
            AisolverBottomSheet(
                onDismissRequest = { onAction(WorkspaceAction.EditBottomSheetDismiss) },
                onItemClick = { item ->
                    onAction(
                        WorkspaceAction.EditBottomSheetValueClick(
                            value = item.toCellValue(),
                        ),
                    )
                },
            )
        }

        if (state.isSettingsDialogVisible) {
            AisolverSettingsDialog(
                spawnTileItem = AisolverSettingsItemModel(
                    title = "Spawn Tile",
                    description = "After each valid move, add a new tile to an empty cell like the real game.",
                    checked = state.isSpawnTileEnabled,
                ),
                autoAnalyzeItem = AisolverSettingsItemModel(
                    title = "Auto Analyze",
                    description = "Automatically run analysis whenever the board state changes.",
                    checked = state.isAutoAnalyzeEnabled,
                ),
                onDismissRequest = { onAction(WorkspaceAction.SettingsDialogDismiss) },
                onSpawnTileCheckedChange = { enabled ->
                    onAction(WorkspaceAction.SpawnTileSettingToggle(enabled = enabled))
                },
                onAutoAnalyzeCheckedChange = { enabled ->
                    onAction(WorkspaceAction.AutoAnalyzeSettingToggle(enabled = enabled))
                },
            )
        }
    }
}

@Composable
private fun WorkspaceStatusSection(
    score: Int,
    canUndo: Boolean,
    canReset: Boolean,
    canOpenSettings: Boolean,
    onUndoClick: () -> Unit,
    onResetClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .width(AisolverBoardDefaults.BoardSize)
            .background(defaultAisolverColorScheme.background.primary),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AisolverScoreCard(
            score = score,
            modifier = Modifier.weight(1f),
        )
        AisolverGameActions(
            onUndoClick = onUndoClick,
            onResetClick = onResetClick,
            onSettingsClick = onSettingsClick,
            undoEnabled = canUndo,
            resetEnabled = canReset,
            settingsEnabled = canOpenSettings,
        )
    }
}

private fun List<WorkspaceBoardTileUi>.toBoardTiles(): List<AisolverBoardTile> =
    map { tile ->
        AisolverBoardTile(
            id = tile.id,
            value = tile.value,
            position = tile.cellIndex.toBoardPosition(),
            previousPosition = tile.previousCellIndex?.toBoardPosition(),
            motionState = tile.motionState,
        )
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

private fun AisolverBoardSwipeDirection.toWorkspaceDirection(): WorkspaceRecommendationDirection =
    when (this) {
        AisolverBoardSwipeDirection.Up -> WorkspaceRecommendationDirection.Up
        AisolverBoardSwipeDirection.Right -> WorkspaceRecommendationDirection.Right
        AisolverBoardSwipeDirection.Left -> WorkspaceRecommendationDirection.Left
        AisolverBoardSwipeDirection.Down -> WorkspaceRecommendationDirection.Down
    }

private fun AisolverRecommendationDirection.toWorkspaceDirection(): WorkspaceRecommendationDirection =
    when (this) {
        AisolverRecommendationDirection.Up -> WorkspaceRecommendationDirection.Up
        AisolverRecommendationDirection.Right -> WorkspaceRecommendationDirection.Right
        AisolverRecommendationDirection.Left -> WorkspaceRecommendationDirection.Left
        AisolverRecommendationDirection.Down -> WorkspaceRecommendationDirection.Down
    }

private fun AisolverBottomSheetItem.toCellValue(): Int =
    when (this) {
        AisolverBottomSheetItem.Clear -> 0
        is AisolverBottomSheetItem.Value -> value
    }

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun WorkspaceScreenPreview() {
    val previewBoardValues = listOf(
        2, 0, 4, 0,
        0, 0, 0, 0,
        0, 0, 0, 0,
        0, 0, 0, 0,
    )

    AiSolver2048Theme {
        WorkspaceScreen(
            state = WorkspaceState(
                boardValues = previewBoardValues,
                boardTiles = previewBoardValues.toPreviewBoardTiles(),
                score = 8,
                editingCellIndex = null,
                isEditBottomSheetVisible = false,
                isSettingsDialogVisible = false,
                canUndo = true,
                canReset = true,
                canAnalyze = true,
                isAnalyzeAvailable = true,
                isAnalyzing = false,
                isInteractionLocked = false,
                isSpawnTileEnabled = true,
                isAutoAnalyzeEnabled = true,
                animateRecommendationChanges = true,
                recommendations = listOf(
                    WorkspaceRecommendationUi(
                        direction = AisolverRecommendationDirection.Left,
                        confidencePercent = 42.8f,
                    ),
                    WorkspaceRecommendationUi(
                        direction = AisolverRecommendationDirection.Right,
                        confidencePercent = 28.4f,
                    ),
                    WorkspaceRecommendationUi(
                        direction = AisolverRecommendationDirection.Up,
                        confidencePercent = 18.1f,
                    ),
                    WorkspaceRecommendationUi(
                        direction = AisolverRecommendationDirection.Down,
                        confidencePercent = 10.7f,
                    ),
                ),
            ),
            onAction = {},
        )
    }
}

private fun List<Int>.toPreviewBoardTiles(): List<WorkspaceBoardTileUi> =
    mapIndexedNotNull { cellIndex, value ->
        value.takeIf { tileValue -> tileValue != 0 }?.let { tileValue ->
            WorkspaceBoardTileUi(
                id = "tile-$cellIndex",
                value = tileValue,
                cellIndex = cellIndex,
            )
        }
    }

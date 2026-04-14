package io.github.helpigstar.aisolver2048.ui.workspace.feature.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.helpigstar.aisolver2048.core.model.MoveDirection
import io.github.helpigstar.aisolver2048.data.workspace.manager.WorkspaceSnapshot
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBoard
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBoardDefaults
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBoardPosition
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBoardTile
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBottomSheet
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBottomSheetItem
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverGameActions
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverRecommendation
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverRecommendationCard
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverScoreCard
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverSettingsDialog
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverSettingsItemModel
import io.github.helpigstar.aisolver2048.ui.platform.components.button.AisolverAutoAnalyzeButtonVariant
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
    val isAutoMoveEnabled = state.isAutoMoveEnabled
    val canEditBoardCells =
        !isAutoMoveEnabled && !state.isInteractionLocked && !state.isEditBottomSheetVisible
    val canTriggerManualMove =
        !isAutoMoveEnabled && !state.isInteractionLocked && !state.isEditBottomSheetVisible && state.canAnalyze
    val canOpenSettings =
        !isAutoMoveEnabled && !state.isInteractionLocked && !state.isEditBottomSheetVisible
    val canUseAnalyzeButton = state.canAnalyze &&
            state.isAnalyzeAvailable &&
            !state.isInteractionLocked &&
            !state.isEditBottomSheetVisible &&
            !isAutoMoveEnabled
    val canUseAutoButton = if (isAutoMoveEnabled) {
        true
    } else {
        canUseAnalyzeButton && state.isAutoAnalyzeEnabled
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = defaultAisolverColorScheme.background.primary,
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .imePadding()
                .padding(top = 12.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            val sharedWidth = minOf(maxWidth, AisolverBoardDefaults.BoardSize)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                WorkspaceStatusSection(
                    score = state.score,
                    width = sharedWidth,
                    canUndo = !isAutoMoveEnabled &&
                            state.canUndo &&
                            !state.isInteractionLocked &&
                            !state.isEditBottomSheetVisible,
                    canReset = !isAutoMoveEnabled &&
                            state.canReset &&
                            !state.isInteractionLocked &&
                            !state.isEditBottomSheetVisible,
                    canOpenSettings = canOpenSettings,
                    onUndoClick = { onAction(WorkspaceAction.UndoClick) },
                    onResetClick = { onAction(WorkspaceAction.ResetClick) },
                    onSettingsClick = { onAction(WorkspaceAction.SettingsClick) },
                )
                AisolverBoard(
                    tiles = state.boardTiles.toBoardTiles(),
                    boardSize = sharedWidth,
                    animateTileChanges = state.isAnimationsEnabled,
                    onCellClick = if (canEditBoardCells) { position ->
                        onAction(WorkspaceAction.CellClick(position.toCellIndex()))
                    } else {
                        null
                    },
                    onSwipe = if (canTriggerManualMove) { direction ->
                        onAction(WorkspaceAction.Move(direction))
                    } else {
                        null
                    },
                )
                AisolverRecommendationCard(
                    recommendations = state.recommendations.toRecommendationModels(),
                    onAnalyzeClick = { onAction(WorkspaceAction.AnalyzeClick) },
                    onAutoMoveClick = { onAction(WorkspaceAction.AutoMoveButtonClick) },
                    onRecommendationClick = if (canTriggerManualMove) { direction ->
                        onAction(WorkspaceAction.Move(direction))
                    } else {
                        null
                    },
                    modifier = Modifier.width(sharedWidth),
                    enabled = canUseAnalyzeButton,
                    autoButtonEnabled = canUseAutoButton,
                    autoButtonVariant = if (isAutoMoveEnabled) {
                        AisolverAutoAnalyzeButtonVariant.Stop
                    } else {
                        AisolverAutoAnalyzeButtonVariant.Auto
                    },
                    analyzeButtonLabel = if (state.isAnalyzing) "Analyzing" else "Analyze",
                    animateRecommendationChanges = state.animateRecommendationChanges &&
                            state.isAnimationsEnabled,
                )
            }
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
                animationsItem = AisolverSettingsItemModel(
                    title = "Animations",
                    description = "Animate board moves and recommendation updates.",
                    checked = state.isAnimationsEnabled,
                ),
                onDismissRequest = { onAction(WorkspaceAction.SettingsDialogDismiss) },
                onSpawnTileCheckedChange = { enabled ->
                    onAction(WorkspaceAction.SpawnTileSettingToggle(enabled = enabled))
                },
                onAutoAnalyzeCheckedChange = { enabled ->
                    onAction(WorkspaceAction.AutoAnalyzeSettingToggle(enabled = enabled))
                },
                onAnimationsCheckedChange = { enabled ->
                    onAction(WorkspaceAction.AnimationsSettingToggle(enabled = enabled))
                },
            )
        }
    }
}

@Composable
private fun WorkspaceStatusSection(
    score: Int,
    width: Dp,
    canUndo: Boolean,
    canReset: Boolean,
    canOpenSettings: Boolean,
    onUndoClick: () -> Unit,
    onResetClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .width(width)
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

private fun AisolverBottomSheetItem.toCellValue(): Int =
    when (this) {
        AisolverBottomSheetItem.Clear -> 0
        is AisolverBottomSheetItem.Value -> value
    }

@PreviewScreenSizes
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
                undoHistory = listOf(
                    WorkspaceSnapshot(
                        boardValues = listOf(
                            2, 0, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0,
                        ),
                        score = 4,
                    ),
                ),
                isAnalyzeAvailable = true,
                isAnalyzing = false,
                isInteractionLocked = false,
                isSpawnTileEnabled = true,
                isAutoAnalyzeEnabled = true,
                isAnimationsEnabled = true,
                isAutoMoveEnabled = false,
                animateRecommendationChanges = true,
                hasFreshRecommendations = true,
                recommendations = listOf(
                    WorkspaceRecommendationUi(
                        direction = MoveDirection.Left,
                        confidencePercent = 42.8f,
                    ),
                    WorkspaceRecommendationUi(
                        direction = MoveDirection.Right,
                        confidencePercent = 28.4f,
                    ),
                    WorkspaceRecommendationUi(
                        direction = MoveDirection.Up,
                        confidencePercent = 18.1f,
                    ),
                    WorkspaceRecommendationUi(
                        direction = MoveDirection.Down,
                        confidencePercent = 10.7f,
                    ),
                ),
                activeAutoAnalyzeRequestId = null,
                nextAutoAnalyzeRequestId = 0L,
                activeMoveAnimationId = null,
                nextMoveAnimationId = 0L,
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

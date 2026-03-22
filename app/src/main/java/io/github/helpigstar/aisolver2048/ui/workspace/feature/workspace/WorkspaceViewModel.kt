package io.github.helpigstar.aisolver2048.ui.workspace.feature.workspace

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.helpigstar.aisolver2048.data.workspace.manager.WorkspaceManager
import io.github.helpigstar.aisolver2048.data.workspace.manager.WorkspaceMoveTile
import io.github.helpigstar.aisolver2048.data.workspace.manager.WorkspaceMoveTileMotionState
import io.github.helpigstar.aisolver2048.data.workspace.manager.WorkspaceRecommendationDirection
import io.github.helpigstar.aisolver2048.data.workspace.manager.WorkspaceRecommendationProbability
import io.github.helpigstar.aisolver2048.data.workspace.manager.WorkspaceSnapshot
import io.github.helpigstar.aisolver2048.ui.platform.base.BaseViewModel
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBoardDefaults
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBoardTileMotionState
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverRecommendationDirection
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

private const val KEY_STATE: String = "state"
private const val KEY_HISTORY: String = "history"
private const val EMPTY_CELL_VALUE: Int = 0
private val MOVE_ANIMATION_DURATION_MILLIS: Long =
    AisolverBoardDefaults.MoveDurationMillis.toLong()
private val MERGE_ANIMATION_DURATION_MILLIS: Long =
    (AisolverBoardDefaults.MergePopDurationMillis * 2).toLong()

@HiltViewModel
class WorkspaceViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val workspaceManager: WorkspaceManager,
) : BaseViewModel<WorkspaceState, Unit, WorkspaceAction>(
    initialState = savedStateHandle
        .get<WorkspaceState>(KEY_STATE)
        ?.restoreAfterProcessDeath()
        ?: workspaceManager
            .createInitialSnapshot()
            .toState(
                selectedCellIndex = null,
                canUndo = false,
            ),
) {
    private val undoHistory: ArrayList<WorkspaceSnapshot> =
        savedStateHandle.get<ArrayList<WorkspaceSnapshot>>(KEY_HISTORY) ?: arrayListOf()

    init {
        mutableStateFlow.update { currentState ->
            currentState.withAvailability(canUndo = undoHistory.isNotEmpty())
        }

        stateFlow
            .onEach { savedStateHandle[KEY_STATE] = it }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: WorkspaceAction) {
        when (action) {
            is WorkspaceAction.CellClick -> handleCellClick(action.cellIndex)
            is WorkspaceAction.Move -> handleMove(direction = action.direction)
            WorkspaceAction.ClearSelectedCellClick -> updateSelectedCellValue(EMPTY_CELL_VALUE)
            WorkspaceAction.ResetClick -> handleResetClick()
            WorkspaceAction.AnalyzeClick -> handleAnalyzeClick()
            WorkspaceAction.SetSelectedCellValueToFourClick -> updateSelectedCellValue(4)
            WorkspaceAction.SetSelectedCellValueToTwoClick -> updateSelectedCellValue(2)
            WorkspaceAction.UndoClick -> handleUndoClick()
        }
    }

    private fun handleCellClick(cellIndex: Int) {
        if (state.isInteractionLocked) return

        mutableStateFlow.update { currentState ->
            currentState.copy(
                selectedCellIndex = if (currentState.selectedCellIndex == cellIndex) {
                    null
                } else {
                    cellIndex
                },
            )
        }
    }

    private fun updateSelectedCellValue(value: Int) {
        if (state.isInteractionLocked) return

        val selectedCellIndex = state.selectedCellIndex ?: return
        val currentSnapshot = state.toSnapshot()
        val updatedSnapshot = workspaceManager.updateCell(
            snapshot = currentSnapshot,
            cellIndex = selectedCellIndex,
            value = value,
        )

        if (updatedSnapshot == currentSnapshot) return

        pushUndoSnapshot(currentSnapshot)
        mutableStateFlow.update {
            updatedSnapshot.toState(
                selectedCellIndex = selectedCellIndex,
                canUndo = undoHistory.isNotEmpty(),
                recommendations = state.recommendations.toPlaceholderRecommendations(),
            )
        }
    }

    private fun handleUndoClick() {
        if (state.isInteractionLocked || undoHistory.isEmpty()) return

        val restoredSnapshot = undoHistory.removeAt(undoHistory.lastIndex)
        persistUndoHistory()
        mutableStateFlow.update {
            restoredSnapshot.toState(
                selectedCellIndex = null,
                canUndo = undoHistory.isNotEmpty(),
                recommendations = state.recommendations.toPlaceholderRecommendations(),
            )
        }
    }

    private fun handleResetClick() {
        if (state.isInteractionLocked) return

        val resetSnapshot = workspaceManager.reset()
        val currentSnapshot = state.toSnapshot()
        if (currentSnapshot == resetSnapshot) return

        pushUndoSnapshot(currentSnapshot)
        mutableStateFlow.update {
            resetSnapshot.toState(
                selectedCellIndex = null,
                canUndo = undoHistory.isNotEmpty(),
                recommendations = state.recommendations.toPlaceholderRecommendations(),
            )
        }
    }

    private fun handleAnalyzeClick() {
        if (state.isInteractionLocked || !state.canAnalyze) return

        val generatedRecommendations = workspaceManager
            .generateRecommendations(snapshot = state.toSnapshot())
            .map { recommendation ->
                recommendation.toUiModel()
            }

        mutableStateFlow.update { currentState ->
            currentState.copy(
                recommendations = generatedRecommendations,
                animateRecommendationChanges = true,
            )
        }
    }

    private fun handleMove(
        direction: WorkspaceRecommendationDirection,
    ) {
        if (
            state.isInteractionLocked ||
            state.selectedCellIndex != null ||
            !canMove(boardValues = state.boardValues)
        ) {
            return
        }

        val currentSnapshot = state.toSnapshot()
        val moveResult = workspaceManager.applyMove(
            snapshot = currentSnapshot,
            direction = direction,
        )
        if (!moveResult.hasChanged) return

        pushUndoSnapshot(currentSnapshot)

        val placeholderRecommendations = state.recommendations.toPlaceholderRecommendations()
        val stageOneBoardTiles = moveResult.stageOneTiles.toUiBoardTiles()
        val finalAnimatedBoardTiles = moveResult.finalTiles.toUiBoardTiles()
        val settledBoardTiles = finalAnimatedBoardTiles.toSettledBoardTiles()
        val hasMergedTiles = moveResult.finalTiles.hasMergedTiles()

        mutableStateFlow.update {
            moveResult.snapshot.toState(
                selectedCellIndex = null,
                canUndo = undoHistory.isNotEmpty(),
                recommendations = placeholderRecommendations,
                boardTiles = stageOneBoardTiles,
                isInteractionLocked = true,
            )
        }

        viewModelScope.launch {
            delay(MOVE_ANIMATION_DURATION_MILLIS)

            if (hasMergedTiles) {
                mutableStateFlow.update { currentState ->
                    currentState.copy(boardTiles = finalAnimatedBoardTiles)
                }
                delay(MERGE_ANIMATION_DURATION_MILLIS)
            }

            mutableStateFlow.update { currentState ->
                currentState.copy(
                    boardTiles = settledBoardTiles,
                    isInteractionLocked = false,
                )
            }
        }
    }

    private fun pushUndoSnapshot(snapshot: WorkspaceSnapshot) {
        undoHistory.add(snapshot)
        persistUndoHistory()
    }

    private fun persistUndoHistory() {
        savedStateHandle[KEY_HISTORY] = ArrayList(undoHistory)
    }
}

@Parcelize
data class WorkspaceState(
    val boardValues: List<Int>,
    val boardTiles: List<WorkspaceBoardTileUi>,
    val score: Int,
    val selectedCellIndex: Int?,
    val canUndo: Boolean,
    val canReset: Boolean,
    val canAnalyze: Boolean,
    val isInteractionLocked: Boolean,
    val animateRecommendationChanges: Boolean,
    val recommendations: List<WorkspaceRecommendationUi>,
) : Parcelable

@Parcelize
data class WorkspaceBoardTileUi(
    val id: String,
    val value: Int,
    val cellIndex: Int,
    val previousCellIndex: Int? = null,
    val motionState: AisolverBoardTileMotionState = AisolverBoardTileMotionState.Static,
) : Parcelable

@Parcelize
data class WorkspaceRecommendationUi(
    val direction: AisolverRecommendationDirection,
    val confidencePercent: Float,
) : Parcelable

sealed class WorkspaceAction {
    data class CellClick(val cellIndex: Int) : WorkspaceAction()

    data class Move(val direction: WorkspaceRecommendationDirection) : WorkspaceAction()

    data object UndoClick : WorkspaceAction()

    data object ResetClick : WorkspaceAction()

    data object AnalyzeClick : WorkspaceAction()

    data object ClearSelectedCellClick : WorkspaceAction()

    data object SetSelectedCellValueToTwoClick : WorkspaceAction()

    data object SetSelectedCellValueToFourClick : WorkspaceAction()
}

private fun WorkspaceState.toSnapshot(): WorkspaceSnapshot =
    WorkspaceSnapshot(
        boardValues = boardValues,
        score = score,
    )

private fun WorkspaceSnapshot.toState(
    selectedCellIndex: Int?,
    canUndo: Boolean,
    recommendations: List<WorkspaceRecommendationUi> = defaultWorkspaceRecommendations(),
    boardTiles: List<WorkspaceBoardTileUi> = boardValues.toStaticBoardTiles(),
    isInteractionLocked: Boolean = false,
): WorkspaceState =
    WorkspaceState(
        boardValues = boardValues,
        boardTiles = boardTiles,
        score = score,
        selectedCellIndex = selectedCellIndex,
        canUndo = canUndo,
        canReset = canReset(
            boardValues = boardValues,
            score = score,
        ),
        canAnalyze = canAnalyze(boardValues),
        isInteractionLocked = isInteractionLocked,
        animateRecommendationChanges = false,
        recommendations = recommendations,
    )

private fun WorkspaceState.restoreAfterProcessDeath(): WorkspaceState =
    if (isInteractionLocked) {
        copy(
            boardTiles = boardValues.toStaticBoardTiles(),
            selectedCellIndex = null,
            isInteractionLocked = false,
            animateRecommendationChanges = false,
            canReset = canReset(
                boardValues = boardValues,
                score = score,
            ),
            canAnalyze = canAnalyze(boardValues),
        )
    } else {
        copy(
            boardTiles = boardValues.toStaticBoardTiles(),
            animateRecommendationChanges = false,
            canReset = canReset(
                boardValues = boardValues,
                score = score,
            ),
            canAnalyze = canAnalyze(boardValues),
        )
    }

private fun WorkspaceState.withAvailability(
    canUndo: Boolean,
): WorkspaceState =
    copy(
        canUndo = canUndo,
        canReset = canReset(
            boardValues = boardValues,
            score = score,
        ),
        canAnalyze = canAnalyze(boardValues),
    )

private fun canReset(
    boardValues: List<Int>,
    score: Int,
): Boolean = boardValues.any { value -> value != EMPTY_CELL_VALUE } || score != 0

private fun canAnalyze(
    boardValues: List<Int>,
): Boolean = boardValues.any { value -> value != EMPTY_CELL_VALUE }

private fun canMove(
    boardValues: List<Int>,
): Boolean = canAnalyze(boardValues)

private fun defaultWorkspaceRecommendations(): List<WorkspaceRecommendationUi> =
    listOf(
        WorkspaceRecommendationUi(
            direction = AisolverRecommendationDirection.Up,
            confidencePercent = 0f,
        ),
        WorkspaceRecommendationUi(
            direction = AisolverRecommendationDirection.Right,
            confidencePercent = 0f,
        ),
        WorkspaceRecommendationUi(
            direction = AisolverRecommendationDirection.Left,
            confidencePercent = 0f,
        ),
        WorkspaceRecommendationUi(
            direction = AisolverRecommendationDirection.Down,
            confidencePercent = 0f,
        ),
    )

private fun List<Int>.toStaticBoardTiles(): List<WorkspaceBoardTileUi> =
    mapIndexedNotNull { cellIndex, value ->
        value.takeIf { tileValue -> tileValue != EMPTY_CELL_VALUE }?.let { tileValue ->
            WorkspaceBoardTileUi(
                id = "tile-$cellIndex",
                value = tileValue,
                cellIndex = cellIndex,
            )
        }
    }

private fun List<WorkspaceMoveTile>.toUiBoardTiles(): List<WorkspaceBoardTileUi> =
    map { tile ->
        WorkspaceBoardTileUi(
            id = tile.id,
            value = tile.value,
            cellIndex = tile.cellIndex,
            previousCellIndex = tile.previousCellIndex,
            motionState = tile.motionState.toUiMotionState(),
        )
    }

private fun List<WorkspaceBoardTileUi>.toSettledBoardTiles(): List<WorkspaceBoardTileUi> =
    map { tile ->
        tile.copy(
            previousCellIndex = null,
            motionState = AisolverBoardTileMotionState.Static,
        )
    }

private fun List<WorkspaceMoveTile>.hasMergedTiles(): Boolean =
    any { tile -> tile.motionState == WorkspaceMoveTileMotionState.Merged }

private fun List<WorkspaceRecommendationUi>.toPlaceholderRecommendations(): List<WorkspaceRecommendationUi> =
    map { recommendation ->
        recommendation.copy(confidencePercent = 0f)
    }

private fun WorkspaceMoveTileMotionState.toUiMotionState(): AisolverBoardTileMotionState =
    when (this) {
        WorkspaceMoveTileMotionState.Static -> AisolverBoardTileMotionState.Static
        WorkspaceMoveTileMotionState.Merged -> AisolverBoardTileMotionState.Merged
    }

private fun WorkspaceRecommendationProbability.toUiModel(): WorkspaceRecommendationUi =
    WorkspaceRecommendationUi(
        direction = direction.toUiDirection(),
        confidencePercent = confidencePercent,
    )

private fun WorkspaceRecommendationDirection.toUiDirection(): AisolverRecommendationDirection =
    when (this) {
        WorkspaceRecommendationDirection.Up -> AisolverRecommendationDirection.Up
        WorkspaceRecommendationDirection.Right -> AisolverRecommendationDirection.Right
        WorkspaceRecommendationDirection.Left -> AisolverRecommendationDirection.Left
        WorkspaceRecommendationDirection.Down -> AisolverRecommendationDirection.Down
    }

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
import io.github.helpigstar.aisolver2048.data.workspace.manager.WorkspaceRecommendationResult
import io.github.helpigstar.aisolver2048.data.workspace.manager.WorkspaceSnapshot
import io.github.helpigstar.aisolver2048.data.workspace.settings.repository.WorkspaceSettingsRepository
import io.github.helpigstar.aisolver2048.data.workspace.settings.repository.model.WorkspaceSettings
import io.github.helpigstar.aisolver2048.ui.platform.base.BaseViewModel
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBoardDefaults
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBoardTileMotionState
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverRecommendationDirection
import kotlinx.coroutines.Job
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
private val SPAWN_ANIMATION_DURATION_MILLIS: Long =
    AisolverBoardDefaults.SpawnDurationMillis.toLong()

@HiltViewModel
class WorkspaceViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val workspaceManager: WorkspaceManager,
    private val workspaceSettingsRepository: WorkspaceSettingsRepository,
) : BaseViewModel<WorkspaceState, Unit, WorkspaceAction>(
    initialState = savedStateHandle
        .get<WorkspaceState>(KEY_STATE)
        ?.restoreAfterProcessDeath()
        ?: workspaceManager
            .createInitialSnapshot()
            .toState(
                editingCellIndex = null,
                isEditBottomSheetVisible = false,
                isSettingsDialogVisible = false,
                canUndo = false,
                workspaceSettings = workspaceSettingsRepository.getWorkspaceSettings(),
            ),
) {
    private val undoHistory: ArrayList<WorkspaceSnapshot> =
        savedStateHandle.get<ArrayList<WorkspaceSnapshot>>(KEY_HISTORY) ?: arrayListOf()
    private var autoAnalyzeJob: Job? = null
    private var activeAutoAnalyzeRequestId: Long? = null
    private var nextAutoAnalyzeRequestId: Long = 0L

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
            WorkspaceAction.SettingsClick -> handleSettingsClick()
            WorkspaceAction.SettingsDialogDismiss -> dismissSettingsDialog()
            is WorkspaceAction.SpawnTileSettingToggle -> updateSpawnTileSetting(action.enabled)
            is WorkspaceAction.AutoAnalyzeSettingToggle -> updateAutoAnalyzeSetting(action.enabled)
            WorkspaceAction.EditBottomSheetDismiss -> dismissEditBottomSheet()
            is WorkspaceAction.EditBottomSheetValueClick -> updateEditingCellValue(action.value)
            is WorkspaceAction.Move -> handleMove(direction = action.direction)
            WorkspaceAction.ResetClick -> handleResetClick()
            WorkspaceAction.AnalyzeClick -> handleAnalyzeClick()
            WorkspaceAction.UndoClick -> handleUndoClick()
        }
    }

    private fun handleSettingsClick() {
        if (state.isInteractionLocked || state.isEditBottomSheetVisible || state.isSettingsDialogVisible) return

        mutableStateFlow.update { currentState ->
            currentState.copy(isSettingsDialogVisible = true)
        }
    }

    private fun dismissSettingsDialog() {
        mutableStateFlow.update { currentState ->
            currentState.copy(isSettingsDialogVisible = false)
        }
    }

    private fun handleCellClick(cellIndex: Int) {
        if (state.isInteractionLocked || state.isEditBottomSheetVisible) return

        mutableStateFlow.update { currentState ->
            currentState.copy(
                editingCellIndex = cellIndex,
                isEditBottomSheetVisible = true,
            )
        }
    }

    private fun dismissEditBottomSheet() {
        mutableStateFlow.update { currentState ->
            currentState.copy(
                editingCellIndex = null,
                isEditBottomSheetVisible = false,
            )
        }
    }

    private fun updateEditingCellValue(value: Int) {
        if (state.isInteractionLocked) return

        val editingCellIndex = state.editingCellIndex ?: return
        val currentSnapshot = state.toSnapshot()
        val updatedSnapshot = workspaceManager.updateCell(
            snapshot = currentSnapshot,
            cellIndex = editingCellIndex,
            value = value,
        )

        if (updatedSnapshot == currentSnapshot) {
            dismissEditBottomSheet()
            return
        }

        pushUndoSnapshot(currentSnapshot)
        mutableStateFlow.update {
            updatedSnapshot.toState(
                editingCellIndex = null,
                isEditBottomSheetVisible = false,
                isSettingsDialogVisible = false,
                canUndo = undoHistory.isNotEmpty(),
                recommendations = state.recommendations.toPlaceholderRecommendations(),
                isAnalyzeAvailable = state.isAnalyzeAvailable,
                workspaceSettings = state.toWorkspaceSettings(),
            )
        }
        requestAutoAnalyzeIfEnabled(snapshot = updatedSnapshot)
    }

    private fun handleUndoClick() {
        if (state.isInteractionLocked || state.isEditBottomSheetVisible || undoHistory.isEmpty()) return

        val restoredSnapshot = undoHistory.removeAt(undoHistory.lastIndex)
        persistUndoHistory()
        mutableStateFlow.update {
            restoredSnapshot.toState(
                editingCellIndex = null,
                isEditBottomSheetVisible = false,
                isSettingsDialogVisible = false,
                canUndo = undoHistory.isNotEmpty(),
                recommendations = state.recommendations.toPlaceholderRecommendations(),
                isAnalyzeAvailable = state.isAnalyzeAvailable,
                workspaceSettings = state.toWorkspaceSettings(),
            )
        }
        requestAutoAnalyzeIfEnabled(snapshot = restoredSnapshot)
    }

    private fun handleResetClick() {
        if (state.isInteractionLocked || state.isEditBottomSheetVisible) return

        val resetSnapshot = workspaceManager.reset()
        val currentSnapshot = state.toSnapshot()
        if (currentSnapshot == resetSnapshot) return

        pushUndoSnapshot(currentSnapshot)
        mutableStateFlow.update {
            resetSnapshot.toState(
                editingCellIndex = null,
                isEditBottomSheetVisible = false,
                isSettingsDialogVisible = false,
                canUndo = undoHistory.isNotEmpty(),
                recommendations = state.recommendations.toPlaceholderRecommendations(),
                isAnalyzeAvailable = state.isAnalyzeAvailable,
                workspaceSettings = state.toWorkspaceSettings(),
            )
        }
        requestAutoAnalyzeIfEnabled(snapshot = resetSnapshot)
    }

    private fun handleAnalyzeClick() {
        cancelAutoAnalyze()
        if (
            state.isInteractionLocked ||
            state.isEditBottomSheetVisible ||
            !state.canAnalyze ||
            !state.isAnalyzeAvailable
        ) {
            return
        }

        val snapshot = state.toSnapshot()
        mutableStateFlow.update { currentState ->
            currentState.copy(
                isInteractionLocked = true,
                isAnalyzing = true,
                animateRecommendationChanges = false,
            )
        }

        viewModelScope.launch {
            when (val recommendationResult = workspaceManager.generateRecommendations(snapshot = snapshot)) {
                WorkspaceRecommendationResult.InferenceFailed -> {
                    mutableStateFlow.update { currentState ->
                        currentState.copy(
                            recommendations = currentState.recommendations.toPlaceholderRecommendations(),
                            isInteractionLocked = false,
                            isAnalyzing = false,
                            animateRecommendationChanges = false,
                        )
                    }
                }

                WorkspaceRecommendationResult.Unavailable -> {
                    mutableStateFlow.update { currentState ->
                        currentState.copy(
                            recommendations = currentState.recommendations.toPlaceholderRecommendations(),
                            isAnalyzeAvailable = false,
                            isInteractionLocked = false,
                            isAnalyzing = false,
                            animateRecommendationChanges = false,
                        )
                    }
                }

                is WorkspaceRecommendationResult.Success -> {
                    val generatedRecommendations = recommendationResult.recommendations.map { recommendation ->
                        recommendation.toUiModel()
                    }
                    mutableStateFlow.update { currentState ->
                        currentState.copy(
                            recommendations = generatedRecommendations,
                            isInteractionLocked = false,
                            isAnalyzing = false,
                            animateRecommendationChanges = true,
                        )
                    }
                }
            }
        }
    }

    private fun handleMove(
        direction: WorkspaceRecommendationDirection,
    ) {
        if (
            state.isInteractionLocked ||
            state.isEditBottomSheetVisible ||
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
        val settledMoveBoardTiles = finalAnimatedBoardTiles.toSettledBoardTiles()
        val hasMergedTiles = moveResult.finalTiles.hasMergedTiles()
        val spawnResult = if (state.isSpawnTileEnabled) {
            workspaceManager.spawnRandomTile(snapshot = moveResult.snapshot)
        } else {
            null
        }
        val finalSnapshot = spawnResult?.snapshot ?: moveResult.snapshot
        val spawnAnimatedBoardTiles = spawnResult
            ?.spawnedTile
            ?.toUiBoardTile()
            ?.let { spawnedTile ->
                settledMoveBoardTiles + spawnedTile
            }
        val settledBoardTiles = spawnAnimatedBoardTiles
            ?.toSettledBoardTiles()
            ?: settledMoveBoardTiles

        mutableStateFlow.update {
            finalSnapshot.toState(
                editingCellIndex = null,
                isEditBottomSheetVisible = false,
                isSettingsDialogVisible = false,
                canUndo = undoHistory.isNotEmpty(),
                recommendations = placeholderRecommendations,
                boardTiles = stageOneBoardTiles,
                isAnalyzeAvailable = state.isAnalyzeAvailable,
                isInteractionLocked = true,
                workspaceSettings = state.toWorkspaceSettings(),
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

            if (spawnAnimatedBoardTiles != null) {
                mutableStateFlow.update { currentState ->
                    currentState.copy(boardTiles = spawnAnimatedBoardTiles)
                }
                delay(SPAWN_ANIMATION_DURATION_MILLIS)
            }

            mutableStateFlow.update { currentState ->
                currentState.copy(
                    boardTiles = settledBoardTiles,
                    isInteractionLocked = false,
                )
            }

            requestAutoAnalyzeIfEnabled(snapshot = finalSnapshot)
        }
    }

    private fun updateSpawnTileSetting(enabled: Boolean) {
        if (enabled == state.isSpawnTileEnabled) return

        updateWorkspaceSettings { currentSettings ->
            currentSettings.copy(isSpawnTileEnabled = enabled)
        }
    }

    private fun updateAutoAnalyzeSetting(enabled: Boolean) {
        if (enabled == state.isAutoAnalyzeEnabled) return

        val previousSettings = state.toWorkspaceSettings()
        updateWorkspaceSettings { currentSettings ->
            currentSettings.copy(isAutoAnalyzeEnabled = enabled)
        }

        if (!enabled) {
            cancelAutoAnalyze()
            return
        }

        if (!previousSettings.isAutoAnalyzeEnabled) {
            requestAutoAnalyzeFromCurrentState()
        }
    }

    private fun updateWorkspaceSettings(
        transform: (WorkspaceSettings) -> WorkspaceSettings,
    ) {
        val updatedSettings = transform(state.toWorkspaceSettings())
        workspaceSettingsRepository.storeWorkspaceSettings(workspaceSettings = updatedSettings)
        mutableStateFlow.update { currentState ->
            currentState.copy(
                isSpawnTileEnabled = updatedSettings.isSpawnTileEnabled,
                isAutoAnalyzeEnabled = updatedSettings.isAutoAnalyzeEnabled,
            )
        }
    }

    private fun requestAutoAnalyzeFromCurrentState() {
        if (!state.canAnalyze || !state.isAnalyzeAvailable || !state.isAutoAnalyzeEnabled) return

        mutableStateFlow.update { currentState ->
            currentState.copy(
                recommendations = currentState.recommendations.toPlaceholderRecommendations(),
                animateRecommendationChanges = false,
            )
        }
        requestAutoAnalyze(snapshot = state.toSnapshot())
    }

    private fun requestAutoAnalyzeIfEnabled(snapshot: WorkspaceSnapshot) {
        if (
            !state.isAutoAnalyzeEnabled ||
            !state.isAnalyzeAvailable ||
            !canAnalyze(snapshot.boardValues)
        ) {
            return
        }

        requestAutoAnalyze(snapshot = snapshot)
    }

    private fun requestAutoAnalyze(snapshot: WorkspaceSnapshot) {
        cancelAutoAnalyze()

        val requestId = ++nextAutoAnalyzeRequestId
        activeAutoAnalyzeRequestId = requestId
        autoAnalyzeJob = viewModelScope.launch {
            when (val recommendationResult = workspaceManager.generateRecommendations(snapshot = snapshot)) {
                WorkspaceRecommendationResult.InferenceFailed -> {
                    if (!isCurrentAutoAnalyzeRequest(requestId = requestId)) return@launch
                    clearAutoAnalyzeRequest(requestId = requestId)
                    mutableStateFlow.update { currentState ->
                        currentState.copy(
                            recommendations = currentState.recommendations.toPlaceholderRecommendations(),
                            animateRecommendationChanges = false,
                        )
                    }
                }

                WorkspaceRecommendationResult.Unavailable -> {
                    if (!isCurrentAutoAnalyzeRequest(requestId = requestId)) return@launch
                    clearAutoAnalyzeRequest(requestId = requestId)
                    mutableStateFlow.update { currentState ->
                        currentState.copy(
                            recommendations = currentState.recommendations.toPlaceholderRecommendations(),
                            isAnalyzeAvailable = false,
                            animateRecommendationChanges = false,
                        )
                    }
                }

                is WorkspaceRecommendationResult.Success -> {
                    if (!isCurrentAutoAnalyzeRequest(requestId = requestId)) return@launch
                    clearAutoAnalyzeRequest(requestId = requestId)
                    val generatedRecommendations = recommendationResult.recommendations.map { recommendation ->
                        recommendation.toUiModel()
                    }
                    mutableStateFlow.update { currentState ->
                        currentState.copy(
                            recommendations = generatedRecommendations,
                            animateRecommendationChanges = true,
                        )
                    }
                }
            }
        }
    }

    private fun cancelAutoAnalyze() {
        autoAnalyzeJob?.cancel()
        autoAnalyzeJob = null
        activeAutoAnalyzeRequestId = null
    }

    private fun isCurrentAutoAnalyzeRequest(
        requestId: Long,
    ): Boolean = activeAutoAnalyzeRequestId == requestId

    private fun clearAutoAnalyzeRequest(requestId: Long) {
        if (activeAutoAnalyzeRequestId == requestId) {
            activeAutoAnalyzeRequestId = null
            autoAnalyzeJob = null
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
    val editingCellIndex: Int?,
    val isEditBottomSheetVisible: Boolean,
    val isSettingsDialogVisible: Boolean,
    val canUndo: Boolean,
    val canReset: Boolean,
    val canAnalyze: Boolean,
    val isAnalyzeAvailable: Boolean,
    val isAnalyzing: Boolean,
    val isInteractionLocked: Boolean,
    val isSpawnTileEnabled: Boolean,
    val isAutoAnalyzeEnabled: Boolean,
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

    data object SettingsClick : WorkspaceAction()

    data object SettingsDialogDismiss : WorkspaceAction()

    data class SpawnTileSettingToggle(val enabled: Boolean) : WorkspaceAction()

    data class AutoAnalyzeSettingToggle(val enabled: Boolean) : WorkspaceAction()

    data object EditBottomSheetDismiss : WorkspaceAction()

    data class EditBottomSheetValueClick(val value: Int) : WorkspaceAction()

    data class Move(val direction: WorkspaceRecommendationDirection) : WorkspaceAction()

    data object UndoClick : WorkspaceAction()

    data object ResetClick : WorkspaceAction()

    data object AnalyzeClick : WorkspaceAction()
}

private fun WorkspaceState.toSnapshot(): WorkspaceSnapshot =
    WorkspaceSnapshot(
        boardValues = boardValues,
        score = score,
    )

private fun WorkspaceSnapshot.toState(
    editingCellIndex: Int?,
    isEditBottomSheetVisible: Boolean,
    isSettingsDialogVisible: Boolean,
    canUndo: Boolean,
    workspaceSettings: WorkspaceSettings,
    recommendations: List<WorkspaceRecommendationUi> = defaultWorkspaceRecommendations(),
    boardTiles: List<WorkspaceBoardTileUi> = boardValues.toStaticBoardTiles(),
    isAnalyzeAvailable: Boolean = true,
    isAnalyzing: Boolean = false,
    isInteractionLocked: Boolean = false,
): WorkspaceState =
    WorkspaceState(
        boardValues = boardValues,
        boardTiles = boardTiles,
        score = score,
        editingCellIndex = editingCellIndex,
        isEditBottomSheetVisible = isEditBottomSheetVisible,
        isSettingsDialogVisible = isSettingsDialogVisible,
        canUndo = canUndo,
        canReset = canReset(
            boardValues = boardValues,
            score = score,
        ),
        canAnalyze = canAnalyze(boardValues),
        isAnalyzeAvailable = isAnalyzeAvailable,
        isAnalyzing = isAnalyzing,
        isInteractionLocked = isInteractionLocked,
        isSpawnTileEnabled = workspaceSettings.isSpawnTileEnabled,
        isAutoAnalyzeEnabled = workspaceSettings.isAutoAnalyzeEnabled,
        animateRecommendationChanges = false,
        recommendations = recommendations,
    )

private fun WorkspaceState.restoreAfterProcessDeath(): WorkspaceState =
    if (isInteractionLocked) {
        copy(
            boardTiles = boardValues.toStaticBoardTiles(),
            editingCellIndex = null,
            isEditBottomSheetVisible = false,
            isSettingsDialogVisible = false,
            isAnalyzeAvailable = true,
            isAnalyzing = false,
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
            isSettingsDialogVisible = false,
            isAnalyzeAvailable = true,
            isAnalyzing = false,
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
    AisolverRecommendationDirection.entries.map { direction ->
        WorkspaceRecommendationUi(
            direction = direction,
            confidencePercent = 0f,
        )
    }

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

private fun WorkspaceMoveTile.toUiBoardTile(): WorkspaceBoardTileUi =
    WorkspaceBoardTileUi(
        id = id,
        value = value,
        cellIndex = cellIndex,
        previousCellIndex = previousCellIndex,
        motionState = motionState.toUiMotionState(),
    )

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
        WorkspaceMoveTileMotionState.Spawned -> AisolverBoardTileMotionState.Spawned
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

private fun WorkspaceState.toWorkspaceSettings(): WorkspaceSettings =
    WorkspaceSettings(
        isSpawnTileEnabled = isSpawnTileEnabled,
        isAutoAnalyzeEnabled = isAutoAnalyzeEnabled,
    )

package io.github.helpigstar.aisolver2048.ui.workspace.feature.workspace

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.helpigstar.aisolver2048.core.model.MoveDirection
import io.github.helpigstar.aisolver2048.data.workspace.manager.WorkspaceManager
import io.github.helpigstar.aisolver2048.data.workspace.manager.WorkspaceMoveTile
import io.github.helpigstar.aisolver2048.data.workspace.manager.WorkspaceMoveTileMotionState
import io.github.helpigstar.aisolver2048.data.workspace.manager.WorkspaceRecommendationProbability
import io.github.helpigstar.aisolver2048.data.workspace.manager.WorkspaceRecommendationResult
import io.github.helpigstar.aisolver2048.data.workspace.manager.WorkspaceSnapshot
import io.github.helpigstar.aisolver2048.data.workspace.settings.repository.WorkspaceSettingsRepository
import io.github.helpigstar.aisolver2048.data.workspace.settings.repository.model.WorkspaceSettings
import io.github.helpigstar.aisolver2048.ui.platform.base.BaseViewModel
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBoardDefaults
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverBoardTileMotionState
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverRecommendationItemDefaults
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverRecommendationListDefaults
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject


private const val KEY_STATE = "state"
private const val MAX_UNDO_HISTORY = 50
private const val EMPTY_CELL_VALUE: Int = 0


private val MOVE_ANIMATION_DURATION_MILLIS: Long =
    AisolverBoardDefaults.MoveDurationMillis.toLong()
private val MERGE_ANIMATION_DURATION_MILLIS: Long =
    (AisolverBoardDefaults.MergePopDurationMillis * 2).toLong()
private val SPAWN_ANIMATION_DURATION_MILLIS: Long =
    AisolverBoardDefaults.SpawnDurationMillis.toLong()
private val RECOMMENDATION_ANIMATION_DURATION_MILLIS: Long =
    maxOf(
        AisolverRecommendationItemDefaults.ValueAnimationDurationMillis,
        AisolverRecommendationListDefaults.PlacementAnimationDurationMillis,
    ).toLong()

@HiltViewModel
class WorkspaceViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val workspaceManager: WorkspaceManager,
    private val workspaceSettingsRepository: WorkspaceSettingsRepository,
) : BaseViewModel<WorkspaceState, Unit, WorkspaceAction>(
    initialState = savedStateHandle.get<WorkspaceSavedState>(KEY_STATE)
        ?.toRestoredState(
            workspaceSettings = workspaceSettingsRepository.getWorkspaceSettings(),
        )
        ?: workspaceManager.createInitialSnapshot()
            .toState(
                editingCellIndex = null,
                isEditBottomSheetVisible = false,
                isSettingsDialogVisible = false,
                undoHistory = emptyList(),
                workspaceSettings = workspaceSettingsRepository.getWorkspaceSettings(),
            ),
) {
    private var autoAnalyzeJob: Job? = null
    private var autoMoveExecutionJob: Job? = null

    init {
        stateFlow
            .onEach(::persistState)
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: WorkspaceAction) {
        when (action) {
            is WorkspaceAction.CellClick -> handleCellClick(action.cellIndex)
            WorkspaceAction.SettingsClick -> handleSettingsClick()
            WorkspaceAction.SettingsDialogDismiss -> dismissSettingsDialog()
            is WorkspaceAction.SpawnTileSettingToggle -> updateSpawnTileSetting(action.enabled)
            is WorkspaceAction.AutoAnalyzeSettingToggle -> updateAutoAnalyzeSetting(action.enabled)
            is WorkspaceAction.AnimationsSettingToggle -> updateAnimationsSetting(action.enabled)
            WorkspaceAction.EditBottomSheetDismiss -> dismissEditBottomSheet()
            is WorkspaceAction.EditBottomSheetValueClick -> updateEditingCellValue(action.value)
            is WorkspaceAction.Move -> handleMove(direction = action.direction)
            WorkspaceAction.ResetClick -> handleResetClick()
            WorkspaceAction.AnalyzeClick -> handleAnalyzeClick()
            WorkspaceAction.AutoMoveButtonClick -> handleAutoMoveButtonClick()
            WorkspaceAction.UndoClick -> handleUndoClick()
            is WorkspaceAction.Internal -> handleInternalAction(action)
        }
    }

    private fun handleInternalAction(action: WorkspaceAction.Internal) {
        when (action) {
            is WorkspaceAction.Internal.AnalyzeResultReceive -> handleAnalyzeResultReceive(action)
            is WorkspaceAction.Internal.AutoAnalyzeResultReceive -> handleAutoAnalyzeResultReceive(
                action
            )

            is WorkspaceAction.Internal.AutoMoveExecute -> handleAutoMoveExecute(action)
            is WorkspaceAction.Internal.MoveAnimationMergePhase -> handleMoveAnimationMergePhase(
                action
            )

            is WorkspaceAction.Internal.MoveAnimationSpawnPhase -> handleMoveAnimationSpawnPhase(
                action
            )

            is WorkspaceAction.Internal.MoveAnimationComplete -> handleMoveAnimationComplete(action)
        }
    }

    private fun handleAnalyzeResultReceive(
        action: WorkspaceAction.Internal.AnalyzeResultReceive,
    ) {
        when (val recommendationResult = action.result) {
            WorkspaceRecommendationResult.InferenceFailed -> {
                mutableStateFlow.update { currentState ->
                    currentState.copy(
                        recommendations = currentState.recommendations.toPlaceholderRecommendations(),
                        isInteractionLocked = false,
                        isAnalyzing = false,
                        animateRecommendationChanges = false,
                        hasFreshRecommendations = false,
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
                        hasFreshRecommendations = false,
                    )
                }
            }

            is WorkspaceRecommendationResult.Success -> {
                val generatedRecommendations =
                    recommendationResult.recommendations.map { recommendation ->
                        recommendation.toUiModel()
                    }
                mutableStateFlow.update { currentState ->
                    currentState.copy(
                        recommendations = generatedRecommendations,
                        isInteractionLocked = false,
                        isAnalyzing = false,
                        animateRecommendationChanges = currentState.isAnimationsEnabled,
                        hasFreshRecommendations = true,
                    )
                }
                scheduleAutoMoveFromRecommendations(
                    waitForRecommendationAnimation = state.isAutoMoveEnabled && state.isAnimationsEnabled,
                )
            }
        }
    }

    private fun clearAutoAnalyzeRequest(requestId: Long) {
        if (!isCurrentAutoAnalyzeRequest(requestId)) return

        autoAnalyzeJob = null
        mutableStateFlow.update { currentState ->
            currentState.copy(activeAutoAnalyzeRequestId = null)
        }
    }

    private fun cancelAutoAnalyze() {
        autoAnalyzeJob?.cancel()
        autoAnalyzeJob = null
        mutableStateFlow.update { currentState ->
            currentState.copy(activeAutoAnalyzeRequestId = null)
        }
    }


    private fun handleAutoAnalyzeResultReceive(
        action: WorkspaceAction.Internal.AutoAnalyzeResultReceive,
    ) {
        if (!isCurrentAutoAnalyzeRequest(requestId = action.requestId)) return
        clearAutoAnalyzeRequest(requestId = action.requestId)

        when (val recommendationResult = action.result) {
            WorkspaceRecommendationResult.InferenceFailed -> {
                mutableStateFlow.update { currentState ->
                    currentState.copy(
                        recommendations = currentState.recommendations.toPlaceholderRecommendations(),
                        animateRecommendationChanges = false,
                        hasFreshRecommendations = false,
                    )
                }
                stopAutoMove(cancelInFlightAnalysis = false)
            }

            WorkspaceRecommendationResult.Unavailable -> {
                mutableStateFlow.update { currentState ->
                    currentState.copy(
                        recommendations = currentState.recommendations.toPlaceholderRecommendations(),
                        isAnalyzeAvailable = false,
                        animateRecommendationChanges = false,
                        hasFreshRecommendations = false,
                    )
                }
                stopAutoMove(cancelInFlightAnalysis = false)
            }

            is WorkspaceRecommendationResult.Success -> {
                val generatedRecommendations =
                    recommendationResult.recommendations.map { recommendation ->
                        recommendation.toUiModel()
                    }
                mutableStateFlow.update { currentState ->
                    currentState.copy(
                        recommendations = generatedRecommendations,
                        animateRecommendationChanges = currentState.isAnimationsEnabled,
                        hasFreshRecommendations = true,
                    )
                }
                scheduleAutoMoveFromRecommendations(
                    waitForRecommendationAnimation = state.isAutoMoveEnabled && state.isAnimationsEnabled,
                )
            }
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

        val updatedUndoHistory = state.undoHistory.pushUndoSnapshot(currentSnapshot)
        mutableStateFlow.update { currentState ->
            updatedSnapshot.toState(
                editingCellIndex = null,
                isEditBottomSheetVisible = false,
                isSettingsDialogVisible = false,
                undoHistory = updatedUndoHistory,
                recommendations = currentState.recommendations.toPlaceholderRecommendations(),
                isAnalyzeAvailable = currentState.isAnalyzeAvailable,
                workspaceSettings = currentState.toWorkspaceSettings(),
                isAutoMoveEnabled = currentState.isAutoMoveEnabled,
                nextAutoAnalyzeRequestId = currentState.nextAutoAnalyzeRequestId,
                nextMoveAnimationId = currentState.nextMoveAnimationId,
            )
        }
        requestAutoAnalyzeIfEnabled(snapshot = updatedSnapshot)
    }


    private fun handleUndoClick() {
        if (state.isInteractionLocked || state.isEditBottomSheetVisible || state.undoHistory.isEmpty()) return

        val restoredSnapshot = state.undoHistory.last()
        val updatedUndoHistory = state.undoHistory.dropLast(1)
        mutableStateFlow.update { currentState ->
            restoredSnapshot.toState(
                editingCellIndex = null,
                isEditBottomSheetVisible = false,
                isSettingsDialogVisible = false,
                undoHistory = updatedUndoHistory,
                recommendations = currentState.recommendations.toPlaceholderRecommendations(),
                isAnalyzeAvailable = currentState.isAnalyzeAvailable,
                workspaceSettings = currentState.toWorkspaceSettings(),
                isAutoMoveEnabled = currentState.isAutoMoveEnabled,
                nextAutoAnalyzeRequestId = currentState.nextAutoAnalyzeRequestId,
                nextMoveAnimationId = currentState.nextMoveAnimationId,
            )
        }
        requestAutoAnalyzeIfEnabled(snapshot = restoredSnapshot)
    }


    private fun handleResetClick() {
        if (state.isInteractionLocked || state.isEditBottomSheetVisible) return

        val resetSnapshot = workspaceManager.reset()
        val currentSnapshot = state.toSnapshot()
        if (currentSnapshot == resetSnapshot) return

        val updatedUndoHistory = state.undoHistory.pushUndoSnapshot(currentSnapshot)
        mutableStateFlow.update { currentState ->
            resetSnapshot.toState(
                editingCellIndex = null,
                isEditBottomSheetVisible = false,
                isSettingsDialogVisible = false,
                undoHistory = updatedUndoHistory,
                recommendations = currentState.recommendations.toPlaceholderRecommendations(),
                isAnalyzeAvailable = currentState.isAnalyzeAvailable,
                workspaceSettings = currentState.toWorkspaceSettings(),
                isAutoMoveEnabled = currentState.isAutoMoveEnabled,
                nextAutoAnalyzeRequestId = currentState.nextAutoAnalyzeRequestId,
                nextMoveAnimationId = currentState.nextMoveAnimationId,
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
            val recommendationResult =
                workspaceManager.generateRecommendations(snapshot = snapshot)
            sendAction(
                WorkspaceAction.Internal.AnalyzeResultReceive(
                    result = recommendationResult,
                ),
            )
        }
    }

    private fun handleAutoMoveButtonClick() {
        if (state.isAutoMoveEnabled) {
            stopAutoMove()
            return
        }

        if (!canEnableAutoMove(state = state)) return

        mutableStateFlow.update { currentState ->
            currentState.copy(isAutoMoveEnabled = true)
        }
        startAutoMoveFromCurrentState()
    }

    private fun handleMove(
        direction: MoveDirection,
    ) {
        if (
            state.isInteractionLocked ||
            state.isEditBottomSheetVisible ||
            !canMove(boardValues = state.boardValues)
        ) {
            return
        }

        val currentSnapshot = state.toSnapshot()
        val updatedUndoHistory = state.undoHistory.pushUndoSnapshot(currentSnapshot)
        val moveResult = workspaceManager.applyMove(
            snapshot = currentSnapshot,
            direction = direction,
        )
        if (!moveResult.hasChanged) return

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

        if (!state.isAnimationsEnabled) {
            mutableStateFlow.update {
                finalSnapshot.toState(
                    editingCellIndex = null,
                    isEditBottomSheetVisible = false,
                    isSettingsDialogVisible = false,
                    undoHistory = updatedUndoHistory,
                    recommendations = placeholderRecommendations,
                    boardTiles = settledBoardTiles,
                    isAnalyzeAvailable = state.isAnalyzeAvailable,
                    workspaceSettings = state.toWorkspaceSettings(),
                    isAutoMoveEnabled = state.isAutoMoveEnabled,
                    hasFreshRecommendations = false,
                )
            }
            requestAutoAnalyzeIfEnabled(snapshot = finalSnapshot)
            return
        }

        mutableStateFlow.update {
            finalSnapshot.toState(
                editingCellIndex = null,
                isEditBottomSheetVisible = false,
                isSettingsDialogVisible = false,
                undoHistory = updatedUndoHistory,
                recommendations = placeholderRecommendations,
                boardTiles = stageOneBoardTiles,
                isAnalyzeAvailable = state.isAnalyzeAvailable,
                isInteractionLocked = true,
                workspaceSettings = state.toWorkspaceSettings(),
                isAutoMoveEnabled = state.isAutoMoveEnabled,
                hasFreshRecommendations = false,
            )
        }

        viewModelScope.launch {
            launchMoveAnimation(
                hasMergedTiles = hasMergedTiles,
                finalAnimatedBoardTiles = finalAnimatedBoardTiles,
                spawnAnimatedBoardTiles = spawnAnimatedBoardTiles,
                settledBoardTiles = settledBoardTiles,
                finalSnapshot = finalSnapshot,
            )
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
            stopAutoMove()
            return
        }

        if (!previousSettings.isAutoAnalyzeEnabled) {
            requestAutoAnalyzeFromCurrentState()
        }
    }

    private fun updateAnimationsSetting(enabled: Boolean) {
        if (enabled == state.isAnimationsEnabled) return

        updateWorkspaceSettings { currentSettings ->
            currentSettings.copy(isAnimationsEnabled = enabled)
        }
        mutableStateFlow.update { currentState ->
            currentState.copy(
                animateRecommendationChanges = currentState.animateRecommendationChanges && enabled,
            )
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
                isAnimationsEnabled = updatedSettings.isAnimationsEnabled,
            )
        }
    }

    private fun requestAutoAnalyzeFromCurrentState() {
        if (!state.canAnalyze || !state.isAnalyzeAvailable || !state.isAutoAnalyzeEnabled) return

        mutableStateFlow.update { currentState ->
            currentState.copy(
                recommendations = currentState.recommendations.toPlaceholderRecommendations(),
                animateRecommendationChanges = false,
                hasFreshRecommendations = false,
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

    private fun handleAutoMoveExecute(
        action: WorkspaceAction.Internal.AutoMoveExecute,
    ) {
        if (!state.isAutoMoveEnabled) return

        if (!canEnableAutoMove(state = state)) {
            stopAutoMove()
            return
        }

        handleMove(direction = action.direction)
    }

    private fun handleMoveAnimationMergePhase(
        action: WorkspaceAction.Internal.MoveAnimationMergePhase,
    ) {
        if (!isCurrentMoveAnimation(animationId = action.animationId)) return

        mutableStateFlow.update { currentState ->
            currentState.copy(boardTiles = action.boardTiles)
        }
    }

    private fun handleMoveAnimationSpawnPhase(
        action: WorkspaceAction.Internal.MoveAnimationSpawnPhase,
    ) {
        if (!isCurrentMoveAnimation(animationId = action.animationId)) return

        mutableStateFlow.update { currentState ->
            currentState.copy(boardTiles = action.boardTiles)
        }
    }

    private fun handleMoveAnimationComplete(
        action: WorkspaceAction.Internal.MoveAnimationComplete,
    ) {
        if (!isCurrentMoveAnimation(animationId = action.animationId)) return

        mutableStateFlow.update { currentState ->
            currentState.copy(
                boardTiles = action.boardTiles,
                isInteractionLocked = false,
                activeMoveAnimationId = null,
            )
        }
        requestAutoAnalyzeIfEnabled(snapshot = action.finalSnapshot)
    }


    private fun launchMoveAnimation(
        hasMergedTiles: Boolean,
        finalAnimatedBoardTiles: List<WorkspaceBoardTileUi>,
        spawnAnimatedBoardTiles: List<WorkspaceBoardTileUi>?,
        settledBoardTiles: List<WorkspaceBoardTileUi>,
        finalSnapshot: WorkspaceSnapshot,
    ) {
        val animationId = state.nextMoveAnimationId + 1
        mutableStateFlow.update { currentState ->
            currentState.copy(
                activeMoveAnimationId = animationId,
                nextMoveAnimationId = animationId,
            )
        }

        viewModelScope.launch {
            delay(MOVE_ANIMATION_DURATION_MILLIS)

            if (hasMergedTiles) {
                sendAction(
                    WorkspaceAction.Internal.MoveAnimationMergePhase(
                        animationId = animationId,
                        boardTiles = finalAnimatedBoardTiles,
                    ),
                )
                delay(MERGE_ANIMATION_DURATION_MILLIS)
            }

            if (spawnAnimatedBoardTiles != null) {
                sendAction(
                    WorkspaceAction.Internal.MoveAnimationSpawnPhase(
                        animationId = animationId,
                        boardTiles = spawnAnimatedBoardTiles,
                    ),
                )
                delay(SPAWN_ANIMATION_DURATION_MILLIS)
            }

            sendAction(
                WorkspaceAction.Internal.MoveAnimationComplete(
                    animationId = animationId,
                    boardTiles = settledBoardTiles,
                    finalSnapshot = finalSnapshot,
                ),
            )
        }
    }

    private fun requestAutoAnalyze(snapshot: WorkspaceSnapshot) {
        cancelAutoAnalyze()

        val requestId = state.nextAutoAnalyzeRequestId + 1
        mutableStateFlow.update { currentState ->
            currentState.copy(
                activeAutoAnalyzeRequestId = requestId,
                nextAutoAnalyzeRequestId = requestId,
            )
        }

        autoAnalyzeJob = viewModelScope.launch {
            val recommendationResult = workspaceManager.generateRecommendations(snapshot = snapshot)
            sendAction(
                WorkspaceAction.Internal.AutoAnalyzeResultReceive(
                    requestId = requestId,
                    result = recommendationResult,
                ),
            )
        }
    }


    private fun startAutoMoveFromCurrentState() {
        if (!state.isAutoMoveEnabled) return

        if (!canEnableAutoMove(state = state)) {
            stopAutoMove()
            return
        }

        if (state.hasFreshRecommendations) {
            scheduleAutoMoveFromRecommendations(waitForRecommendationAnimation = false)
            return
        }

        if (state.isAnalyzing || autoAnalyzeJob != null) return

        requestAutoAnalyzeFromCurrentState()
    }

    private fun scheduleAutoMoveFromRecommendations(
        waitForRecommendationAnimation: Boolean,
    ) {
        if (!state.isAutoMoveEnabled) return
        if (state.isInteractionLocked || state.isEditBottomSheetVisible) return

        val bestDirection = state.recommendations.bestAutoMoveDirectionOrNull()
        if (bestDirection == null) {
            stopAutoMove(cancelInFlightAnalysis = false)
            return
        }

        cancelAutoMoveExecution()
        autoMoveExecutionJob = viewModelScope.launch {
            if (waitForRecommendationAnimation) {
                delay(RECOMMENDATION_ANIMATION_DURATION_MILLIS)
            }

            autoMoveExecutionJob = null
            sendAction(
                WorkspaceAction.Internal.AutoMoveExecute(
                    direction = bestDirection,
                ),
            )
        }
    }

    private fun stopAutoMove(
        cancelInFlightAnalysis: Boolean = true,
    ) {
        cancelAutoMoveExecution()
        if (cancelInFlightAnalysis) {
            cancelAutoAnalyze()
        }
        mutableStateFlow.update { currentState ->
            if (!currentState.isAutoMoveEnabled) {
                currentState
            } else {
                currentState.copy(isAutoMoveEnabled = false)
            }
        }
    }

    private fun cancelAutoMoveExecution() {
        autoMoveExecutionJob?.cancel()
        autoMoveExecutionJob = null
    }

    private fun isCurrentMoveAnimation(
        animationId: Long,
    ): Boolean = state.activeMoveAnimationId == animationId

    private fun isCurrentAutoAnalyzeRequest(
        requestId: Long,
    ): Boolean = state.activeAutoAnalyzeRequestId == requestId


    private fun List<WorkspaceSnapshot>.pushUndoSnapshot(
        snapshot: WorkspaceSnapshot,
    ): List<WorkspaceSnapshot> =
        if (size == MAX_UNDO_HISTORY) {
            drop(1) + snapshot
        } else {
            this + snapshot
        }


    private fun persistState(currentState: WorkspaceState) {
        savedStateHandle[KEY_STATE] = WorkspaceSavedState(
            snapshot = currentState.toSnapshot(),
            undoHistory = currentState.undoHistory,
            editingCellIndex = currentState.editingCellIndex,
            isEditBottomSheetVisible = currentState.isEditBottomSheetVisible,
            isSettingsDialogVisible = currentState.isSettingsDialogVisible,
            isAnalyzeAvailable = currentState.isAnalyzeAvailable,
            hasFreshRecommendations = currentState.hasFreshRecommendations,
            recommendations = currentState.recommendations,
        )
    }

}


data class WorkspaceState(
    val boardValues: List<Int>,
    val boardTiles: List<WorkspaceBoardTileUi>,
    val score: Int,
    val editingCellIndex: Int?,
    val isEditBottomSheetVisible: Boolean,
    val isSettingsDialogVisible: Boolean,
    val undoHistory: List<WorkspaceSnapshot>,
    val isAnalyzeAvailable: Boolean,
    val isAnalyzing: Boolean,
    val isInteractionLocked: Boolean,
    val isSpawnTileEnabled: Boolean,
    val isAutoAnalyzeEnabled: Boolean,
    val isAnimationsEnabled: Boolean,
    val isAutoMoveEnabled: Boolean,
    val animateRecommendationChanges: Boolean,
    val hasFreshRecommendations: Boolean,
    val recommendations: List<WorkspaceRecommendationUi>,
    val activeAutoAnalyzeRequestId: Long?,
    val nextAutoAnalyzeRequestId: Long,
    val activeMoveAnimationId: Long?,
    val nextMoveAnimationId: Long,
) {
    val canUndo: Boolean
        get() = undoHistory.isNotEmpty()
    val canReset: Boolean
        get() = boardValues.any() { it != EMPTY_CELL_VALUE } || score != 0
    val canAnalyze: Boolean
        get() = boardValues.any() { it != EMPTY_CELL_VALUE }
}


data class WorkspaceBoardTileUi(
    val id: String,
    val value: Int,
    val cellIndex: Int,
    val previousCellIndex: Int? = null,
    val motionState: AisolverBoardTileMotionState = AisolverBoardTileMotionState.Static,
)

@Parcelize
data class WorkspaceRecommendationUi(
    val direction: MoveDirection,
    val confidencePercent: Float,
) : Parcelable

@Parcelize
private data class WorkspaceSavedState(
    val snapshot: WorkspaceSnapshot,
    val undoHistory: List<WorkspaceSnapshot>,
    val editingCellIndex: Int?,
    val isEditBottomSheetVisible: Boolean,
    val isSettingsDialogVisible: Boolean,
    val isAnalyzeAvailable: Boolean,
    val hasFreshRecommendations: Boolean,
    val recommendations: List<WorkspaceRecommendationUi>,
) : Parcelable


sealed class WorkspaceAction {
    data class CellClick(val cellIndex: Int) : WorkspaceAction()

    data object SettingsClick : WorkspaceAction()

    data object SettingsDialogDismiss : WorkspaceAction()

    data class SpawnTileSettingToggle(val enabled: Boolean) : WorkspaceAction()

    data class AutoAnalyzeSettingToggle(val enabled: Boolean) : WorkspaceAction()

    data class AnimationsSettingToggle(val enabled: Boolean) : WorkspaceAction()

    data object EditBottomSheetDismiss : WorkspaceAction()

    data class EditBottomSheetValueClick(val value: Int) : WorkspaceAction()

    data class Move(val direction: MoveDirection) : WorkspaceAction()

    data object UndoClick : WorkspaceAction()

    data object ResetClick : WorkspaceAction()

    data object AnalyzeClick : WorkspaceAction()

    data object AutoMoveButtonClick : WorkspaceAction()

    sealed class Internal : WorkspaceAction() {
        data class AnalyzeResultReceive(
            val result: WorkspaceRecommendationResult,
        ) : Internal()

        data class AutoAnalyzeResultReceive(
            val requestId: Long,
            val result: WorkspaceRecommendationResult,
        ) : Internal()

        data class AutoMoveExecute(
            val direction: MoveDirection,
        ) : Internal()

        data class MoveAnimationMergePhase(
            val animationId: Long,
            val boardTiles: List<WorkspaceBoardTileUi>,
        ) : Internal()

        data class MoveAnimationSpawnPhase(
            val animationId: Long,
            val boardTiles: List<WorkspaceBoardTileUi>,
        ) : Internal()

        data class MoveAnimationComplete(
            val animationId: Long,
            val boardTiles: List<WorkspaceBoardTileUi>,
            val finalSnapshot: WorkspaceSnapshot,
        ) : Internal()
    }
}

private fun WorkspaceState.toSnapshot(): WorkspaceSnapshot =
    WorkspaceSnapshot(
        boardValues = boardValues,
        score = score,
    )

private fun WorkspaceSavedState.toRestoredState(
    workspaceSettings: WorkspaceSettings,
): WorkspaceState =
    snapshot.toState(
        editingCellIndex = editingCellIndex,
        isEditBottomSheetVisible = isEditBottomSheetVisible,
        isSettingsDialogVisible = isSettingsDialogVisible,
        workspaceSettings = workspaceSettings,
        recommendations = recommendations,
        boardTiles = snapshot.boardValues.toStaticBoardTiles(),
        isAnalyzeAvailable = isAnalyzeAvailable,
        isAnalyzing = false,
        isInteractionLocked = false,
        isAutoMoveEnabled = false,
        hasFreshRecommendations = hasFreshRecommendations,
        undoHistory = undoHistory,
        activeAutoAnalyzeRequestId = null,
        nextAutoAnalyzeRequestId = 0L,
        activeMoveAnimationId = null,
        nextMoveAnimationId = 0L,
    )

private fun WorkspaceSnapshot.toState(
    editingCellIndex: Int?,
    isEditBottomSheetVisible: Boolean,
    isSettingsDialogVisible: Boolean,
    workspaceSettings: WorkspaceSettings,
    recommendations: List<WorkspaceRecommendationUi> = defaultWorkspaceRecommendations(),
    boardTiles: List<WorkspaceBoardTileUi> = boardValues.toStaticBoardTiles(),
    isAnalyzeAvailable: Boolean = true,
    isAnalyzing: Boolean = false,
    isInteractionLocked: Boolean = false,
    isAutoMoveEnabled: Boolean = false,
    hasFreshRecommendations: Boolean = false,
    undoHistory: List<WorkspaceSnapshot> = emptyList(),
    activeAutoAnalyzeRequestId: Long? = null,
    nextAutoAnalyzeRequestId: Long = 0L,
    activeMoveAnimationId: Long? = null,
    nextMoveAnimationId: Long = 0L,
): WorkspaceState =
    WorkspaceState(
        boardValues = boardValues,
        boardTiles = boardTiles,
        score = score,
        editingCellIndex = editingCellIndex,
        isEditBottomSheetVisible = isEditBottomSheetVisible,
        isSettingsDialogVisible = isSettingsDialogVisible,
        undoHistory = undoHistory,
        isAnalyzeAvailable = isAnalyzeAvailable,
        isAnalyzing = isAnalyzing,
        isInteractionLocked = isInteractionLocked,
        isSpawnTileEnabled = workspaceSettings.isSpawnTileEnabled,
        isAutoAnalyzeEnabled = workspaceSettings.isAutoAnalyzeEnabled,
        isAnimationsEnabled = workspaceSettings.isAnimationsEnabled,
        isAutoMoveEnabled = isAutoMoveEnabled,
        animateRecommendationChanges = false,
        hasFreshRecommendations = hasFreshRecommendations,
        recommendations = recommendations,
        activeAutoAnalyzeRequestId = activeAutoAnalyzeRequestId,
        nextAutoAnalyzeRequestId = nextAutoAnalyzeRequestId,
        activeMoveAnimationId = activeMoveAnimationId,
        nextMoveAnimationId = nextMoveAnimationId,
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
    MoveDirection.entries.map { direction ->
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

private fun List<WorkspaceRecommendationUi>.bestAutoMoveDirectionOrNull(): MoveDirection? =
    firstOrNull { recommendation -> recommendation.confidencePercent > 0f }
        ?.direction

private fun WorkspaceMoveTileMotionState.toUiMotionState(): AisolverBoardTileMotionState =
    when (this) {
        WorkspaceMoveTileMotionState.Static -> AisolverBoardTileMotionState.Static
        WorkspaceMoveTileMotionState.Spawned -> AisolverBoardTileMotionState.Spawned
        WorkspaceMoveTileMotionState.Merged -> AisolverBoardTileMotionState.Merged
    }

private fun WorkspaceRecommendationProbability.toUiModel(): WorkspaceRecommendationUi =
    WorkspaceRecommendationUi(
        direction = direction,
        confidencePercent = confidencePercent,
    )

private fun canEnableAutoMove(
    state: WorkspaceState,
): Boolean = state.isAutoAnalyzeEnabled &&
        state.canAnalyze &&
        state.isAnalyzeAvailable &&
        !state.isInteractionLocked &&
        !state.isEditBottomSheetVisible

private fun WorkspaceState.toWorkspaceSettings(): WorkspaceSettings =
    WorkspaceSettings(
        isSpawnTileEnabled = isSpawnTileEnabled,
        isAutoAnalyzeEnabled = isAutoAnalyzeEnabled,
        isAnimationsEnabled = isAnimationsEnabled,
    )

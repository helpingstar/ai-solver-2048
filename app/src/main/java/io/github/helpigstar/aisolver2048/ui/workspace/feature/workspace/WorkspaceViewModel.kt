package io.github.helpigstar.aisolver2048.ui.workspace.feature.workspace

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.helpigstar.aisolver2048.data.workspace.manager.WorkspaceManager
import io.github.helpigstar.aisolver2048.data.workspace.manager.WorkspaceSnapshot
import io.github.helpigstar.aisolver2048.ui.platform.base.BaseViewModel
import io.github.helpigstar.aisolver2048.ui.platform.components.AisolverRecommendationDirection
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

private const val KEY_STATE: String = "state"
private const val KEY_HISTORY: String = "history"
private const val EMPTY_CELL_VALUE: Int = 0

@HiltViewModel
class WorkspaceViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val workspaceManager: WorkspaceManager,
) : BaseViewModel<WorkspaceState, Unit, WorkspaceAction>(
    initialState = savedStateHandle[KEY_STATE]
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
        mutableStateFlow.update {
            it.copy(
                canUndo = undoHistory.isNotEmpty(),
                canReset = canReset(
                    boardValues = it.boardValues,
                    score = it.score,
                ),
            )
        }

        stateFlow
            .onEach { savedStateHandle[KEY_STATE] = it }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: WorkspaceAction) {
        when (action) {
            is WorkspaceAction.CellClick -> handleCellClick(action.cellIndex)
            WorkspaceAction.ClearSelectedCellClick -> updateSelectedCellValue(EMPTY_CELL_VALUE)
            WorkspaceAction.ResetClick -> handleResetClick()
            WorkspaceAction.SetSelectedCellValueToFourClick -> updateSelectedCellValue(4)
            WorkspaceAction.SetSelectedCellValueToTwoClick -> updateSelectedCellValue(2)
            WorkspaceAction.UndoClick -> handleUndoClick()
        }
    }

    private fun handleCellClick(cellIndex: Int) {
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
            )
        }
    }

    private fun handleUndoClick() {
        if (undoHistory.isEmpty()) return

        val restoredSnapshot = undoHistory.removeAt(undoHistory.lastIndex)
        persistUndoHistory()
        mutableStateFlow.update {
            restoredSnapshot.toState(
                selectedCellIndex = null,
                canUndo = undoHistory.isNotEmpty(),
            )
        }
    }

    private fun handleResetClick() {
        val resetSnapshot = workspaceManager.reset()
        val currentSnapshot = state.toSnapshot()
        if (currentSnapshot == resetSnapshot) return

        pushUndoSnapshot(currentSnapshot)
        mutableStateFlow.update {
            resetSnapshot.toState(
                selectedCellIndex = null,
                canUndo = undoHistory.isNotEmpty(),
            )
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
    val score: Int,
    val selectedCellIndex: Int?,
    val canUndo: Boolean,
    val canReset: Boolean,
    val recommendations: List<WorkspaceRecommendationUi>,
) : Parcelable

@Parcelize
data class WorkspaceRecommendationUi(
    val direction: AisolverRecommendationDirection,
    val confidencePercent: Int,
) : Parcelable

sealed class WorkspaceAction {
    data class CellClick(val cellIndex: Int) : WorkspaceAction()

    data object UndoClick : WorkspaceAction()

    data object ResetClick : WorkspaceAction()

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
): WorkspaceState =
    WorkspaceState(
        boardValues = boardValues,
        score = score,
        selectedCellIndex = selectedCellIndex,
        canUndo = canUndo,
        canReset = canReset(
            boardValues = boardValues,
            score = score,
        ),
        recommendations = defaultWorkspaceRecommendations(),
    )

private fun canReset(
    boardValues: List<Int>,
    score: Int,
): Boolean = boardValues.any { value -> value != EMPTY_CELL_VALUE } || score != 0

private fun defaultWorkspaceRecommendations(): List<WorkspaceRecommendationUi> =
    listOf(
        WorkspaceRecommendationUi(
            direction = AisolverRecommendationDirection.Up,
            confidencePercent = 0,
        ),
        WorkspaceRecommendationUi(
            direction = AisolverRecommendationDirection.Right,
            confidencePercent = 0,
        ),
        WorkspaceRecommendationUi(
            direction = AisolverRecommendationDirection.Left,
            confidencePercent = 0,
        ),
        WorkspaceRecommendationUi(
            direction = AisolverRecommendationDirection.Down,
            confidencePercent = 0,
        ),
    )

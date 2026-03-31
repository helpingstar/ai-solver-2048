package io.github.helpigstar.aisolver2048.data.workspace.manager

import android.os.Parcelable
import io.github.helpigstar.aisolver2048.core.model.MoveDirection
import kotlinx.parcelize.Parcelize

interface WorkspaceManager {
    fun createInitialSnapshot(): WorkspaceSnapshot

    fun updateCell(
        snapshot: WorkspaceSnapshot,
        cellIndex: Int,
        value: Int,
    ): WorkspaceSnapshot

    fun reset(): WorkspaceSnapshot

    fun applyMove(
        snapshot: WorkspaceSnapshot,
        direction: MoveDirection,
    ): WorkspaceMoveResult

    fun spawnRandomTile(
        snapshot: WorkspaceSnapshot,
    ): WorkspaceSpawnResult

    suspend fun generateRecommendations(
        snapshot: WorkspaceSnapshot,
    ): WorkspaceRecommendationResult
}

@Parcelize
data class WorkspaceSnapshot(
    val boardValues: List<Int>,
    val score: Int,
) : Parcelable

data class WorkspaceRecommendationProbability(
    val direction: MoveDirection,
    val confidencePercent: Float,
)

sealed interface WorkspaceRecommendationResult {
    data class Success(
        val recommendations: List<WorkspaceRecommendationProbability>,
    ) : WorkspaceRecommendationResult

    data object InferenceFailed : WorkspaceRecommendationResult

    data object Unavailable : WorkspaceRecommendationResult
}

data class WorkspaceMoveResult(
    val snapshot: WorkspaceSnapshot,
    val stageOneTiles: List<WorkspaceMoveTile>,
    val finalTiles: List<WorkspaceMoveTile>,
    val hasChanged: Boolean,
)

data class WorkspaceSpawnResult(
    val snapshot: WorkspaceSnapshot,
    val spawnedTile: WorkspaceMoveTile?,
)

data class WorkspaceMoveTile(
    val id: String,
    val value: Int,
    val cellIndex: Int,
    val previousCellIndex: Int? = null,
    val motionState: WorkspaceMoveTileMotionState = WorkspaceMoveTileMotionState.Static,
)

enum class WorkspaceMoveTileMotionState {
    Static,
    Spawned,
    Merged,
}

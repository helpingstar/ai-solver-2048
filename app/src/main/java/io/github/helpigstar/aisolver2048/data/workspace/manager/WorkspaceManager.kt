package io.github.helpigstar.aisolver2048.data.workspace.manager

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

interface WorkspaceManager {
    fun createInitialSnapshot(): WorkspaceSnapshot

    fun updateCell(
        snapshot: WorkspaceSnapshot,
        cellIndex: Int,
        value: Int,
    ): WorkspaceSnapshot

    fun reset(): WorkspaceSnapshot

    fun generateRecommendations(
        snapshot: WorkspaceSnapshot,
    ): List<WorkspaceRecommendationProbability>
}

@Parcelize
data class WorkspaceSnapshot(
    val boardValues: List<Int>,
    val score: Int,
) : Parcelable

data class WorkspaceRecommendationProbability(
    val direction: WorkspaceRecommendationDirection,
    val confidencePercent: Float,
)

enum class WorkspaceRecommendationDirection {
    Up,
    Right,
    Left,
    Down,
}

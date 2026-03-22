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
}

@Parcelize
data class WorkspaceSnapshot(
    val boardValues: List<Int>,
    val score: Int,
) : Parcelable

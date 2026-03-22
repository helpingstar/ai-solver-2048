package io.github.helpigstar.aisolver2048.data.workspace.manager

import kotlin.random.Random

private const val BOARD_CELL_COUNT: Int = 16

class WorkspaceManagerImpl : WorkspaceManager {

    override fun createInitialSnapshot(): WorkspaceSnapshot =
        WorkspaceSnapshot(
            boardValues = List(size = BOARD_CELL_COUNT) { 0 },
            score = 0,
        )

    override fun updateCell(
        snapshot: WorkspaceSnapshot,
        cellIndex: Int,
        value: Int,
    ): WorkspaceSnapshot {
        require(cellIndex in 0 until BOARD_CELL_COUNT) {
            "cellIndex must be within 0..15"
        }
        require(isValidBoardValue(value)) {
            "Board values must be empty or a power of two"
        }
        require(snapshot.boardValues.size == BOARD_CELL_COUNT) {
            "Workspace board must contain exactly 16 cells"
        }

        if (snapshot.boardValues[cellIndex] == value) return snapshot

        val updatedBoardValues = snapshot.boardValues.toMutableList().apply {
            this[cellIndex] = value
        }

        return snapshot.copy(boardValues = updatedBoardValues)
    }

    override fun reset(): WorkspaceSnapshot = createInitialSnapshot()

    override fun generateRecommendations(
        snapshot: WorkspaceSnapshot,
    ): List<WorkspaceRecommendationProbability> {
        validateSnapshot(snapshot)

        val rawRecommendations = WorkspaceRecommendationDirection.entries.map { direction ->
            WorkspaceRecommendationProbability(
                direction = direction,
                confidencePercent = Random.nextFloat(),
            )
        }
        val totalConfidence = rawRecommendations.sumOf { recommendation ->
            recommendation.confidencePercent.toDouble()
        }.toFloat()

        return if (totalConfidence <= 0f) {
            rawRecommendations.map { recommendation ->
                recommendation.copy(confidencePercent = 25f)
            }
        } else {
            rawRecommendations.map { recommendation ->
                recommendation.copy(
                    confidencePercent = (recommendation.confidencePercent / totalConfidence) * 100f,
                )
            }
        }.sortedByDescending { recommendation ->
            recommendation.confidencePercent
        }
    }

    private fun isValidBoardValue(value: Int): Boolean =
        value == 0 || (value > 0 && (value and (value - 1)) == 0)

    private fun validateSnapshot(snapshot: WorkspaceSnapshot) {
        require(snapshot.boardValues.size == BOARD_CELL_COUNT) {
            "Workspace board must contain exactly 16 cells"
        }
    }
}

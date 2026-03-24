package io.github.helpigstar.aisolver2048.data.workspace.manager

import io.github.helpigstar.aisolver2048.data.workspace.inference.WorkspaceInferenceResult
import io.github.helpigstar.aisolver2048.data.workspace.inference.WorkspaceInferenceRunner
import kotlin.math.exp
import kotlin.random.Random

private const val BOARD_CELL_COUNT: Int = 16
private const val BOARD_SIDE_LENGTH: Int = 4
private const val SPAWN_TILE_VALUE_TWO = 2
private const val SPAWN_TILE_VALUE_FOUR = 4
private const val SPAWN_TILE_FOUR_PROBABILITY = 0.1f

class WorkspaceManagerImpl(
    private val workspaceInferenceRunner: WorkspaceInferenceRunner,
    private val random: Random = Random.Default,
) : WorkspaceManager {

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

    override fun applyMove(
        snapshot: WorkspaceSnapshot,
        direction: WorkspaceRecommendationDirection,
    ): WorkspaceMoveResult {
        validateSnapshot(snapshot)

        val stageOneTiles = mutableListOf<WorkspaceMoveTile>()
        val finalTiles = mutableListOf<WorkspaceMoveTile>()
        val updatedBoardValues = MutableList(size = BOARD_CELL_COUNT) { 0 }
        var scoreDelta = 0

        lineIndicesFor(direction = direction).forEach { lineIndices ->
            val nonEmptyLineTiles = lineIndices.mapNotNull { cellIndex ->
                snapshot.boardValues[cellIndex]
                    .takeIf { value -> value != 0 }
                    ?.let { value ->
                        IndexedBoardTile(
                            cellIndex = cellIndex,
                            value = value,
                        )
                    }
            }

            var targetOffset = 0
            var sourceOffset = 0
            while (sourceOffset < nonEmptyLineTiles.size) {
                val currentTile = nonEmptyLineTiles[sourceOffset]
                val targetCellIndex = lineIndices[targetOffset]
                val nextTile = nonEmptyLineTiles.getOrNull(sourceOffset + 1)

                if (nextTile != null && currentTile.value == nextTile.value) {
                    stageOneTiles += currentTile.toMergeStageTile(targetCellIndex = targetCellIndex)
                    stageOneTiles += nextTile.toMergeStageTile(targetCellIndex = targetCellIndex)

                    val mergedValue = currentTile.value * 2
                    updatedBoardValues[targetCellIndex] = mergedValue
                    finalTiles += WorkspaceMoveTile(
                        id = finalTileId(cellIndex = targetCellIndex),
                        value = mergedValue,
                        cellIndex = targetCellIndex,
                        motionState = WorkspaceMoveTileMotionState.Merged,
                    )
                    scoreDelta += mergedValue
                    sourceOffset += 2
                } else {
                    updatedBoardValues[targetCellIndex] = currentTile.value
                    stageOneTiles += currentTile.toStageOneTile(targetCellIndex = targetCellIndex)
                    finalTiles += WorkspaceMoveTile(
                        id = finalTileId(cellIndex = targetCellIndex),
                        value = currentTile.value,
                        cellIndex = targetCellIndex,
                    )
                    sourceOffset += 1
                }

                targetOffset += 1
            }
        }

        val updatedSnapshot = snapshot.copy(
            boardValues = updatedBoardValues,
            score = snapshot.score + scoreDelta,
        )
        val hasChanged = updatedSnapshot != snapshot

        return if (hasChanged) {
            WorkspaceMoveResult(
                snapshot = updatedSnapshot,
                stageOneTiles = stageOneTiles,
                finalTiles = finalTiles,
                hasChanged = true,
            )
        } else {
            WorkspaceMoveResult(
                snapshot = snapshot,
                stageOneTiles = snapshot.boardValues.toStaticMoveTiles(),
                finalTiles = snapshot.boardValues.toStaticMoveTiles(),
                hasChanged = false,
            )
        }
    }

    override fun spawnRandomTile(
        snapshot: WorkspaceSnapshot,
    ): WorkspaceSpawnResult {
        validateSnapshot(snapshot)

        val emptyCellIndices = snapshot.boardValues.mapIndexedNotNull { cellIndex, value ->
            cellIndex.takeIf { value == 0 }
        }
        if (emptyCellIndices.isEmpty()) {
            return WorkspaceSpawnResult(
                snapshot = snapshot,
                spawnedTile = null,
            )
        }

        val spawnCellIndex = emptyCellIndices[random.nextInt(until = emptyCellIndices.size)]
        val spawnedValue = if (random.nextFloat() < SPAWN_TILE_FOUR_PROBABILITY) {
            SPAWN_TILE_VALUE_FOUR
        } else {
            SPAWN_TILE_VALUE_TWO
        }
        val updatedBoardValues = snapshot.boardValues.toMutableList().apply {
            this[spawnCellIndex] = spawnedValue
        }

        return WorkspaceSpawnResult(
            snapshot = snapshot.copy(boardValues = updatedBoardValues),
            spawnedTile = WorkspaceMoveTile(
                id = finalTileId(cellIndex = spawnCellIndex),
                value = spawnedValue,
                cellIndex = spawnCellIndex,
                motionState = WorkspaceMoveTileMotionState.Spawned,
            ),
        )
    }

    override suspend fun generateRecommendations(
        snapshot: WorkspaceSnapshot,
    ): WorkspaceRecommendationResult {
        validateSnapshot(snapshot)

        return when (
            val inferenceResult = workspaceInferenceRunner.runInference(
                boardValues = snapshot.boardValues,
            )
        ) {
            WorkspaceInferenceResult.InferenceFailed -> WorkspaceRecommendationResult.InferenceFailed
            WorkspaceInferenceResult.Unavailable -> WorkspaceRecommendationResult.Unavailable
            is WorkspaceInferenceResult.Success -> WorkspaceRecommendationResult.Success(
                recommendations = buildRecommendations(
                    snapshot = snapshot,
                    policyLogits = inferenceResult.policyLogits,
                ),
            )
        }
    }

    private fun buildRecommendations(
        snapshot: WorkspaceSnapshot,
        policyLogits: FloatArray,
    ): List<WorkspaceRecommendationProbability> {
        if (policyLogits.size != WorkspaceRecommendationDirection.entries.size) {
            return zeroRecommendations()
        }

        val legalActionMask = WorkspaceRecommendationDirection.entries.map { direction ->
            applyMove(
                snapshot = snapshot,
                direction = direction,
            ).hasChanged
        }
        val validIndices = legalActionMask.mapIndexedNotNull { index, isValid ->
            index.takeIf { isValid }
        }

        if (validIndices.isEmpty()) {
            return zeroRecommendations()
        }

        val maxValidLogit = validIndices.maxOf { index ->
            policyLogits[index]
        }
        val expValuesByIndex = validIndices.associateWith { index ->
            exp((policyLogits[index] - maxValidLogit).toDouble()).toFloat()
        }
        val totalExp = expValuesByIndex.values.sum()

        if (totalExp <= 0f) {
            return zeroRecommendations()
        }

        return WorkspaceRecommendationDirection.entries.map { direction ->
            val probability = if (legalActionMask[direction.ordinal]) {
                (expValuesByIndex.getValue(direction.ordinal) / totalExp) * 100f
            } else {
                0f
            }
            WorkspaceRecommendationProbability(
                direction = direction,
                confidencePercent = probability,
            )
        }.sortedWith(
            compareByDescending<WorkspaceRecommendationProbability> { recommendation ->
                recommendation.confidencePercent
            }.thenBy { recommendation -> recommendation.direction.ordinal },
        )
    }

    private fun zeroRecommendations(): List<WorkspaceRecommendationProbability> =
        WorkspaceRecommendationDirection.entries.map { direction ->
            WorkspaceRecommendationProbability(
                direction = direction,
                confidencePercent = 0f,
            )
        }

    private fun isValidBoardValue(value: Int): Boolean =
        value == 0 || (value > 0 && (value and (value - 1)) == 0)

    private fun validateSnapshot(snapshot: WorkspaceSnapshot) {
        require(snapshot.boardValues.size == BOARD_CELL_COUNT) {
            "Workspace board must contain exactly 16 cells"
        }
    }

    private fun lineIndicesFor(
        direction: WorkspaceRecommendationDirection,
    ): List<List<Int>> = when (direction) {
        WorkspaceRecommendationDirection.Left -> List(size = BOARD_SIDE_LENGTH) { row ->
            List(size = BOARD_SIDE_LENGTH) { column ->
                (row * BOARD_SIDE_LENGTH) + column
            }
        }

        WorkspaceRecommendationDirection.Right -> List(size = BOARD_SIDE_LENGTH) { row ->
            List(size = BOARD_SIDE_LENGTH) { column ->
                (row * BOARD_SIDE_LENGTH) + (BOARD_SIDE_LENGTH - 1 - column)
            }
        }

        WorkspaceRecommendationDirection.Up -> List(size = BOARD_SIDE_LENGTH) { column ->
            List(size = BOARD_SIDE_LENGTH) { row ->
                (row * BOARD_SIDE_LENGTH) + column
            }
        }

        WorkspaceRecommendationDirection.Down -> List(size = BOARD_SIDE_LENGTH) { column ->
            List(size = BOARD_SIDE_LENGTH) { row ->
                ((BOARD_SIDE_LENGTH - 1 - row) * BOARD_SIDE_LENGTH) + column
            }
        }
    }

    private fun IndexedBoardTile.toStageOneTile(
        targetCellIndex: Int,
    ): WorkspaceMoveTile =
        WorkspaceMoveTile(
            id = finalTileId(cellIndex = targetCellIndex),
            value = value,
            cellIndex = targetCellIndex,
            previousCellIndex = cellIndex.takeIf { sourceCellIndex ->
                sourceCellIndex != targetCellIndex
            },
        )

    private fun IndexedBoardTile.toMergeStageTile(
        targetCellIndex: Int,
    ): WorkspaceMoveTile =
        WorkspaceMoveTile(
            id = mergeSourceTileId(
                sourceCellIndex = cellIndex,
                targetCellIndex = targetCellIndex,
            ),
            value = value,
            cellIndex = targetCellIndex,
            previousCellIndex = cellIndex.takeIf { sourceCellIndex ->
                sourceCellIndex != targetCellIndex
            },
        )

    private fun List<Int>.toStaticMoveTiles(): List<WorkspaceMoveTile> =
        mapIndexedNotNull { cellIndex, value ->
            value.takeIf { tileValue -> tileValue != 0 }?.let { tileValue ->
                WorkspaceMoveTile(
                    id = finalTileId(cellIndex = cellIndex),
                    value = tileValue,
                    cellIndex = cellIndex,
                )
            }
        }

    private fun finalTileId(cellIndex: Int): String = "tile-$cellIndex"

    private fun mergeSourceTileId(
        sourceCellIndex: Int,
        targetCellIndex: Int,
    ): String = "merge-$sourceCellIndex-to-$targetCellIndex"
}

private data class IndexedBoardTile(
    val cellIndex: Int,
    val value: Int,
)

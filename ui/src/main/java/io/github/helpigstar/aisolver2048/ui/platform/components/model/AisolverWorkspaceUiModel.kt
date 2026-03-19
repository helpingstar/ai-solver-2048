package io.github.helpigstar.aisolver2048.ui.platform.components.model

data class AisolverBoardPosition(
    val row: Int,
    val column: Int,
)

enum class AisolverTileMotionState {
    Static,
    New,
    Merged,
}

enum class AisolverControlMode {
    Move,
    Edit,
}

enum class AisolverMoveDirection(
    val label: String,
    val symbol: String,
) {
    Up(label = "Up", symbol = "↑"),
    Right(label = "Right", symbol = "→"),
    Down(label = "Down", symbol = "↓"),
    Left(label = "Left", symbol = "←"),
}

data class AisolverTileUiModel(
    val row: Int,
    val column: Int,
    val value: Int,
    val isSelected: Boolean = false,
    val motionState: AisolverTileMotionState = AisolverTileMotionState.Static,
)

data class AisolverMoveRecommendationUiModel(
    val direction: AisolverMoveDirection,
    val probabilityText: String,
    val expectedScoreText: String,
    val isBest: Boolean = false,
    val isEnabled: Boolean = true,
)

data class AisolverWorkspaceUiModel(
    val score: Int,
    val bestTile: Int,
    val tiles: List<AisolverTileUiModel>,
    val selectedCell: AisolverBoardPosition? = null,
    val bestMove: AisolverMoveDirection? = null,
    val recommendations: List<AisolverMoveRecommendationUiModel> = emptyList(),
    val controlMode: AisolverControlMode = AisolverControlMode.Move,
    val showValuePicker: Boolean = false,
    val scoreDelta: Int? = null,
)

object AisolverWorkspaceSamples {
    fun default(): AisolverWorkspaceUiModel = AisolverWorkspaceUiModel(
        score = 32_480,
        bestTile = 4_096,
        tiles = listOf(
            AisolverTileUiModel(0, 0, 64),
            AisolverTileUiModel(0, 1, 32),
            AisolverTileUiModel(0, 2, 16),
            AisolverTileUiModel(0, 3, 8),
            AisolverTileUiModel(1, 0, 256),
            AisolverTileUiModel(1, 1, 128, motionState = AisolverTileMotionState.New),
            AisolverTileUiModel(1, 2, 64),
            AisolverTileUiModel(1, 3, 4),
            AisolverTileUiModel(2, 0, 512),
            AisolverTileUiModel(2, 1, 1_024),
            AisolverTileUiModel(2, 2, 32),
            AisolverTileUiModel(3, 0, 2),
            AisolverTileUiModel(3, 1, 4),
            AisolverTileUiModel(3, 2, 2_048, motionState = AisolverTileMotionState.Merged),
            AisolverTileUiModel(3, 3, 4_096)
        ),
        bestMove = AisolverMoveDirection.Up,
        recommendations = defaultRecommendations(enabled = true),
        controlMode = AisolverControlMode.Move,
        showValuePicker = false,
        scoreDelta = 128
    )

    fun selected(): AisolverWorkspaceUiModel = default().copy(
        selectedCell = AisolverBoardPosition(row = 2, column = 3),
        bestMove = AisolverMoveDirection.Right,
        recommendations = defaultRecommendations(enabled = false),
        controlMode = AisolverControlMode.Edit,
        scoreDelta = 32
    )

    fun valuePicker(): AisolverWorkspaceUiModel = selected().copy(
        showValuePicker = true
    )

    private fun defaultRecommendations(enabled: Boolean): List<AisolverMoveRecommendationUiModel> = listOf(
        AisolverMoveRecommendationUiModel(
            direction = AisolverMoveDirection.Up,
            probabilityText = "34%",
            expectedScoreText = "4096",
            isBest = true,
            isEnabled = enabled
        ),
        AisolverMoveRecommendationUiModel(
            direction = AisolverMoveDirection.Right,
            probabilityText = "28%",
            expectedScoreText = "3072",
            isEnabled = enabled
        ),
        AisolverMoveRecommendationUiModel(
            direction = AisolverMoveDirection.Down,
            probabilityText = "21%",
            expectedScoreText = "2048",
            isEnabled = enabled
        ),
        AisolverMoveRecommendationUiModel(
            direction = AisolverMoveDirection.Left,
            probabilityText = "17%",
            expectedScoreText = "1536",
            isEnabled = enabled
        )
    )
}
